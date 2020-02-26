#basic processing on facebook metric data set
#import pandas
import pandas as pd

#import matplotlib
import matplotlib.pyplot as plt

data = pd.read_csv("dataset_Facebook.csv",sep=';')

print("-----------------------------HEAD----------------------------")
print(data.head())


print("-----------------------------DESC----------------------------")
print(data.describe())


print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE)----------------------------")
sub1 = data[['Type','comment','like','share']]
print(sub1.head())

print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE) WITH CONDTION THAT THERE SHOULD BE AT LEAST 500 LIKES----------------------------")
sub2 = sub1[sub1.like>500]
print(sub2)

print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE) WITH CONDTION THAT THERE SHOULD BE AT LEAST 50 LIKES COMMENTS AND SHARES----------------------------")
sub3 = sub1[(sub1.like>50)&(sub1.comment>50)&(sub1.share>50)]
print(sub3)

#plot1 = sub3.plot()
#plt.show()
print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE) SUM----------------------------")
sub4 = [sub1.like.sum(),sub1.comment.sum(),sub1.share.sum()]
print(sub4)

#PLOT SHOWING TOTAL NUMBER OF LIKES COMMENTS AND SHRARES

title = ["like","comment","share"]
plt.bar(title,sub4)
plt.gcf().canvas.set_window_title('COMPARISION OF LIKES COMMENT AND SHARES')
plt.show()


#PLOT HISTOGRAM OF COMMENTS

hist = plt.hist(data['comment'],bins=30)
plt.gcf().canvas.set_window_title('Hoistogram of Comments')
plt.show(hist)


#PLOT SCATTER OF CATEGORY VS LIKES

graph = plt.scatter(data['Type'],data['comment'])
plt.xlabel('category')
plt.ylabel('comments')
plt.gcf().canvas.set_window_title('scatter plot : Type vs Comments')
plt.show(graph)

#PLOT SCATTER OF CATEGORY VS TOTAL INTERACTIONS

graph = plt.scatter(data['Type'],data['Total Interactions'])
plt.xlabel('category')
plt.ylabel('interactions')
plt.gcf().canvas.set_window_title('scatter plot : Type vs Interactions')
plt.show(graph)


#PLOT SCATTER OF PAID VS TYPE

graph = plt.scatter(data['Paid'],data['Type'])
plt.xlabel('Paid')
plt.ylabel('Type')
plt.gcf().canvas.set_window_title('scatter plot : paid vs type')
plt.show(graph)
