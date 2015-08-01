#!/bin/bash

#SBATCH --partition=general-compute
#SBATCH --nodes=4
#SBATCH --ntasks-per-node=16
#SBATCH --job-name=OpenMP_Prime	
#SBATCH --time=01:00:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_OpenMP_Prime64.out
#SBATCH --error=Result_OpenMP_Prime64.out

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
module load intel
#
./openmp 100000

#
echo "All Dones!"
