#!/bin/bash

#SBATCH --partition=general-compute
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=8
#SBATCH --job-name=OpenMP_Sort	
#SBATCH --time=00:59:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_OpenMP_Sort.out
#SBATCH --error=Result_OpenMP_Sort.out

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
./OpenMP -t 100000

#
echo "All Dones!"
