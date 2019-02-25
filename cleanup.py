import os

readRatings = open(os.getcwd() + "/Movie Database/ratings.csv", "r").read().split("\n")
ratings = open(os.getcwd() + "/Movie Database/ratings.csv", "w")
readMovies = open(os.getcwd() + "/Movie Database/movies.csv", "r").read().split("\n")
movies = open(os.getcwd() + "/Movie Database/movies.csv", "w")

oldToNew = dict()
count = 1

for line in readMovies:
    data = line.split()


