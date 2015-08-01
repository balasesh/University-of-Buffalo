#!/bin/bash

#SBATCH --partition=debug
#SBATCH --nodes=2
#SBATCH --ntasks-per-node=8
#SBATCH --job-name=OpenMP_MPI
#SBATCH --time=00:30:00
#SBATCH --mail-user=balasesh@buffalo.edu
#SBATCH --output=Result_OpenMP_MPI.out
#SBATCH --error=Result_OpenMP_MPI.out

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

module load intel
module load intel-mpi
module list
ulimit -s unlimited

#
export I_MPI_PMI_LIBRARY=/usr/lib64/libpmi.so
srun ./mpiopenmp 100000
#
echo "All Dones!"
