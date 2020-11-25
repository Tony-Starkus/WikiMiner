"""
This program change the csv file delimiter.
"""
import csv

path = ""

reader = list(csv.reader(open(path, "r"), delimiter=','))
writer = csv.writer(open(path, 'w'), delimiter=';')
writer.writerows(row for row in reader)
