#!/bin/bash

#SBATCH --partition=debug
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --job-name=Sequential
#SBATCH --time=00:59:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_Sequential.out
#SBATCH --error=Result_Sequential.out

echo "SLURN Enviroment Variables:"
echo "Job ID = "$SLURM_JOB_ID
echo "Job Name = "$SLURM_JOB_NAME
echo "Job Node List = "$SLURM_JOB_NODELIST
echo "Number of Nodes = "$SLURM_NNODES
echo "Tasks per Nodes = "$SLURM_NTASKS_PER_NODE
echo "CPUs per task = "$SLURM_CPUS_PER_TASK
echo "/scratch/jobid = "$SLURMTMPDIR
echo "submit Host = "$SLURM_SUBMIT_HOST
echo "Subimt Directory = "$SLURM_SUBMIT_DIR
echo 


ulimit -s unlimited

#

./Sequential -t 100000
#
echo "All Dones!"
