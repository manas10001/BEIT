#import pandas
import pandas as pd

#import matplotlib
#import matplotlib.pyplot as plt

stud = pd.read_csv("students.csv",header=0)
marks = pd.read_csv("marks.csv",header=0)

print("-----------------------------STUDENTS HEAD----------------------------")
print(stud.head(10))


print("-----------------------------MARKS HEAD----------------------------")
print(marks.head(10))


#---------------------------------CLEANING 

#TRANSFORMATION
stud.Class.replace({'1st Class':1,'2nd Class':2,'3rd Class':3},inplace=True)


todrop = ['Age','Gender','Employed']

c_stud = stud.drop(todrop, inplace=False,axis=1)
c_marks = marks.drop('City',inplace=False,axis=1)

print("-----------------------------STUDENTS CLEANED----------------------------")
print(c_stud.head(10))
print("-----------------------------MARKS CLEANED----------------------------")
print(c_marks.head(10))


#--------------------------------INTIGRATION

result = pd.merge(c_stud,c_marks,on='Student_id')


print("-----------------------------RESULTS----------------------------")
print(result.head(10))



