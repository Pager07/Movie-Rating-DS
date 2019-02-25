import os

readRatings = open(os.getcwd() + "/Movie Database/ratings.csv", "r").read().split("\n")
ratings = open(os.getcwd() + "/Movie Database/ratings.csv", "w")
readMovies = open(os.getcwd() + "/Movie Database/movies.csv", "r").read().split("\n")
movies = open(os.getcwd() + "/Movie Database/movies.csv", "w")

oldToNew = dict()
count = 1

for line in readMovies:
    data = line.split(",")
    oldToNew[int(data[0])] = count
    movies.write(str(count) + "," + data[1] + "," + data[2] + "\n")
    count += 1
movies.close()

for line in readRatings:
    data = line.split(",")
    ratings.write(data[0] + "," + str(oldToNew[int(data[1])]) + "," + data[2] + "\n")
ratings.close()