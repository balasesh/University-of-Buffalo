#!/bin/bash
#SBATCH --partition=general-compute
#SBATCH --nodes=2
#SBATCH --tasks-per-node=2
#SBATCH --exclusive
#SBATCH --time=00:15:00
#SBATCH --job-name=mahout_kmeans
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=kmeans.out
#SBATCH --error=kmeans.out

echo "SLURM Environment Variables:"
echo "Job ID = "$SLURM_JOB_ID
echo "Job Name = "$SLURM_JOB_NAME
echo "Job Node List = "$SLURM_JOB_NODELIST
echo "Number of Nodes = "$SLURM_NNODES
echo "Tasks per node = "$SLURM_NTASKS_PER_NODE
echo "CPUs per task = "$SLURM_CPUS_PER_TASK
echo "/scratch/jobid = "$SLURMTMPDIR
echo "Submit Host = "$SLURM_SUBMIT_HOST
echo "Submit Directory = "$SLURM_SUBMIT_DIR
echo 
echo

#. $MODULESHOME/init/sh

#myhadoop is tool to help config and run hadoop.
module load myhadoop/0.2a/hadoop-0.20.1
module load mahout/0.8
module list
echo "MY_HADOOP_HOME="$MY_HADOOP_HOME
echo "HADOOP_HOME="$HADOOP_HOME
echo "MAHOUT_HOME="$MAHOUT_HOME

#### Set this to the directory where Hadoop configs should be generated
# Don't change the name of this variable (HADOOP_CONF_DIR) as it is
# required by Hadoop - all config files will be picked up from here
#
# Make sure that this is accessible to all nodes
export HADOOP_CONF_DIR=$SLURM_SUBMIT_DIR/config
echo "MyHadoop config directory="$HADOOP_CONF_DIR
### Set up the configuration
# Make sure number of nodes is the same as what you have requested from PBS
# usage: $MY_HADOOP_HOME/bin/pbs-configure.sh -h
echo "Set up the configurations for myHadoop"
# this is the non-persistent mode
export PBS_NODEFILE=nodelist.$$
srun --nodes=${SLURM_NNODES} bash -c 'hostname' | sort > $PBS_NODEFILE
NNuniq=`cat $PBS_NODEFILE | uniq | wc -l`
echo "Number of nodes in nodelist="$NNuniq
$MY_HADOOP_HOME/bin/pbs-configure.sh -n $NNuniq -c $HADOOP_CONF_DIR

sleep 5
# this is the persistent mode
# $MY_HADOOP_HOME/bin/pbs-configure.sh -n 4 -c $HADOOP_CONF_DIR -p -d /oasis/cloudstor-group/HDFS

#### Format HDFS, if this is the first time or not a persistent instance
echo
echo "Format HDFS"
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR namenode -format

sleep 15

echo
echo "start dfs"
$HADOOP_HOME/bin/start-dfs.sh

echo
echo "start jobtracker (mapred)"
$HADOOP_HOME/bin/start-mapred.sh



sleep 15
echo
echo "------- make directory ----"
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR dfs -mkdir /data
echo "------- copy training data to dfs---"
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR dfs -put ./tweets-seq /data
sleep 15
echo "------- list files in dfs---"
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR dfs -ls /data

echo "-------Using mahout to transform the training data into vectors using tfidf weights ---"
##### this will generate files in HDFS in the directory /data/tweets-vectors#######
$MAHOUT_HOME/bin/mahout seq2sparse -i /data/tweets-seq -o /data/tweets-vectors --maxDFPercent 85 --namedVector
echo "-------list directory tweets-vectors ---"
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR dfs -ls /data/tweets-vectors

echo " ------Using mahout to splits the sets into two sets: traing and testing sets ---"
##### training set is to train the classifier and testing set is to test the classifier ######
$MAHOUT_HOME/bin/mahout kmeans -i /data/tweets-vectors/tfidf-vectors/ -c /data/tweets-seq/parsedtext-kmeans-clusters -o /data/parsedtext-kmeans -dm org.apache.mahout.common.distance.CosineDistanceMeasure -x 10 -k 7 -ow --clustering -cl

##### Dump the results to file ####

echo " ------Dump the results to file---"
$MAHOUT_HOME/bin/mahout clusterdump -i /data/parsedtext-kmeans/clusters-*-final -d /data/tweets-vectors/dictionary.file-0 -dt sequencefile -b 100 -n 20 --evaluate -dm org.apache.mahout.common.distance.CosineDistanceMeasure --pointsDir /data/parsedtext-kmeans/clusteredPoints -o ./cluster-output.txt

##### Dump the Document to Cluster mapping #####
$HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR dfs -ls /data/parsedtext-kmeans/clusteredPoints/

$MAHOUT_HOME/bin/mahout seqdumper -i /data/parsedtext-kmeans/clusteredPoints/part-m-00000 > cluster-points.txt

##### Check the above results to see the correctly classified instance ####

echo "stop jobtracker (mapred)"
$HADOOP_HOME/bin/stop-mapred.sh

echo "stop dfs"
$HADOOP_HOME/bin/stop-dfs.sh

#### Clean up the working directories after job completion
echo "Clean up"
$MY_HADOOP_HOME/bin/pbs-cleanup.sh -n $NNuniq
echo
