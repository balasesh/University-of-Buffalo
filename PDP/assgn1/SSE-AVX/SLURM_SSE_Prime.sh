#!/bin/bash

#SBATCH --partition=general-compute
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=4
#SBATCH --job-name=SSEPrime
#SBATCH --time=04:00:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_SsePrime.out
#SBATCH --error=Result_SsePrime.out

echo "SLURN Enviroment Variables:"
echo "Job ID = "$SLURM_JOB_ID
echo "Job Name = "$SLURM_JOB_NAME
echo "Job Node List = "$SLURM_JOB_NODELIST
echo "Number of Nodes = "$SLURM_NNODES
echo "Tasks per Nodes = "$SLURM_NTASKS_PER_NODE
echo "/scratch/jobid = "$SLURMTMPDIR
echo "submit Host = "$SLURM_SUBMIT_HOST
echo "Subimt Directory = "$SLURM_SUBMIT_DIR
echo 

ulimit -s unlimited
module load intel
#
./sseprime 1000000000

#
echo "All Dones!"
