#!/bin/bash
# Script to push code to GitHub repository

set -e

echo "Initializing git repository..."
git init

echo "Adding remote..."
git remote add origin https://github.com/rajsumit-2/self-saving.git || git remote set-url origin https://github.com/rajsumit-2/self-saving.git

echo "Adding all files..."
git add .

echo "Creating initial commit..."
git commit -m "Initial commit: Blackrock Challenge Java project"

echo "Setting branch to main..."
git branch -M main

echo "Pushing to GitHub..."
git push -u origin main

echo "Done! Code pushed to https://github.com/rajsumit-2/self-saving"

