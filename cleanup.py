import os

readRatings = open(os.getcwd() + "/Movie Database/ratings.csv", "r").read().split("\n")
ratings = open(os.getcwd() + "/Movie Database/ratings.csv", "w")
data = open(os.getcwd() + "/Movie Database/movies.csv", "r").read().split("\n")

acceptable = set()

for line in data:
    acceptable.add(int(line.split(",")[0]))

for line in readRatings:
    if int(line.split(",")[1]) in acceptable:
        ratings.write(line + "\n")
ratings.close()
