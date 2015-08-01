#!/bin/bash

#SBATCH --partition=general-compute
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=16
#SBATCH --job-name=MPI_Prime10P11
#SBATCH --time=00:59:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_MPI_Prime10p11.out
#SBATCH --error=Result_MPI_Prime10p11.out

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
module load intel-mpi
module list
#
export I_MPI_PMI_LIBRARY=/usr/lib64/libpmi.so
srun ./mpiprime	100000000000

#
echo "All Dones!"
