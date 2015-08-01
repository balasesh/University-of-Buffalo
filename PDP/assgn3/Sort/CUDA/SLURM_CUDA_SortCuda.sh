#!/bin/bash
#SBATCH --partition=gpu
#SBATCH --gres=gpu:2
#SBATCH --nodes=1
#SBATCH --tasks-per-node=1
#SBATCH --job-name=Sort_Cuda
#SBATCH --time=00:30:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_Sort_Cudanew.out
#SBATCH --error=Result_Sort_Cudanew.out

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
module load cuda
ulimit -s unlimited
#
./SortCuda -t 10000

#
echo "All Done!"
