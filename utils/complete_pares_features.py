"""
This program complete the file pares-features.csv with id's not used from the set os articles train. This is necessary to run the WikiMiner Python program.
The algorithm will create a copy of the original file, and them will use the older file.

# SET VARIABLES
- folder: Path to the pares-features.csv file and articlesSetId.csv file.
"""

import csv
import shutil


folder = ""

shutil.copy(folder + "pares-features.csv", folder + "pares-features (original).csv")

IDList = []  # Lista com o ID dos artigos
print("Reading Articles ID's...")
with open(folder + "articlesSetID.csv") as file:
    csv_reader = csv.reader(file)
    for line in csv_reader:
        IDList.append(int(line[0]))
    file.close()
print(f"Total ID's: {len(IDList)}")

total_lines = 0
pairs = []

with open(folder + "pares-features.csv", "r") as csvfile:
    csv_reader = csv.reader(csvfile)
    next(csv_reader)  # Jumping column names
    for line in csv_reader:
        total_lines = total_lines + 1
        pairs.append((int(line[0]), int(line[1])))

with open(folder + "pares-features.csv", "a+") as csvfile:
    aux = 0
    remaining_lines = (len(IDList) * len(IDList)) - total_lines
    writer = csv.writer(csvfile)
    for article in IDList:
        for topic in IDList:
            if (article, topic) not in pairs:
                dados = [article, topic, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'FALSE']
                writer.writerow(dados)
                remaining_lines = remaining_lines - 1
                print(f"{remaining_lines} remaining lines")

                if remaining_lines == 0:
                    break
        if remaining_lines == 0:
            break

with open(folder + "pares-features.csv", "r") as csvfile:
    print(f"{len(csvfile.readlines())} lines")

"""
with open(folder + "pares-features.csv", "a+") as csvfile:
    csvfile.seek(0)
    total_lines = len(csvfile.readlines())
    writer = csv.writer(csvfile)
    aux = 0
    for i in range((len(IDList) * len(IDList)) - total_lines):
        aux = aux + 1
        dados = [-1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'FALSE']
        writer.writerow(dados)
        print(f"{((len(IDList) * len(IDList)) - total_lines) - aux} remaining lines")

"""
