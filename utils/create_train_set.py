"""
Create files containing the ID's of articles that will be for test and train.

# SET VARIABLES
- output_path: where program will save the files.
"""
import csv
import os
output_path = ""


def chunkIt(seq, num):
    avg = len(seq) / float(num)
    out = []
    last = 0.0

    while last < len(seq):
        out.append(seq[int(last):int(last + avg)])
        last += avg

    return out


IDList = []  # Lista com o ID dos artigos
print("Reading Articles ID's...")
with open(output_path + "articlesSetID.csv") as file:
    csv_reader = csv.reader(file)
    for line in csv_reader:
        IDList.append(int(line[0]))
    file.close()

list1 = chunkIt(IDList, 5)

aux = 1
index = 0
train_file = True
for lista in list1:
    print(f"Size of list for file {aux}: {len(lista)}")
    if train_file:
        with open(output_path + f"train_{aux}.txt", "w") as file:
            for _id in lista:
                index += 1
                file.write(f"{index},{_id}\n")
        train_file = False
    else:
        with open(output_path + f"test_{aux}.txt", "w") as file:
            for _id in lista:
                index += 1
                file.write(f"{index},{_id}\n")
        aux += 1

print(f"index: {index}")
print("Done!")
