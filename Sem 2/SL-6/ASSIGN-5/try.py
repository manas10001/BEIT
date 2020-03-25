
#import pandas
import pandas as pd
#import matplotlib
import matplotlib.pyplot as plt

import matplotlib

#matplotlib.use('tkagg')

data = pd.read_csv("dataset_Facebook.csv",sep=';')

print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE)----------------------------")
sub1 = data[['Type','comment','like','share']]
print(sub1.head())

print("-----------------------------SUBSET (TYPE, COMMENT, LIKE, SHARE) SUM----------------------------")
sub4 = [sub1.like.sum(),sub1.comment.sum(),sub1.share.sum()]
print(sub4)

title = ["like","comment","share"]
plt.bar(title,sub4)
plt.gcf().canvas.set_window_title('COMPARISION OF LIKES COMMENT AND SHARES')
plt.savefig('abc.png',bbox_inches='tight')
