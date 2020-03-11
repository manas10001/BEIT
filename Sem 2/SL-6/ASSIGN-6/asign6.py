#import pandas
import pandas as pd

#import matplotlib
import matplotlib.pyplot as plt

data = pd.read_csv("dataset_Facebook.csv",sep=';')

print("-----------------------------HEAD----------------------------")
print(data.head())


print("-----------------------------DESC----------------------------")
print(data.describe())


print("-----------------------------columns----------------------------")
for col in data.columns:
	print(col)


#---------------------------------CLEANING 

todrop = ['Paid', 'Lifetime Post Total Reach', 'Lifetime Post Total Impressions', 'Lifetime Engaged Users', 'Lifetime Post Consumers', 'Lifetime Post Consumptions', 'Lifetime Post Impressions by people who have liked your Page', 'Lifetime Post reach by people who like your Page', 'Lifetime People who have liked your Page and engaged with your post']

cleandata = data.drop(todrop, inplace=False,axis=1)

#print(data.head())
print(cleandata.head())



