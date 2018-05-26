import glob
import csv

def convert_csv(fileType_list):
    for fileType in fileType_list:
        for fileName in glob.glob(fileType):
            fileNameOut = fileName.replace('.csv','x.csv')
            print ('fileName: %s, Fileout: %s'%(fileName,fileNameOut))

            reader = csv.reader(open(fileName, "rU"), delimiter=',')
            new_rows = []
            for row in reader:  # iterate over the rows in the file
                new_row = row  # at first, just copy the row
                new_row = [x.replace('.', ',') for x in new_row]  # make the substitutions
                new_rows.append(new_row)  # add the modified rows
            writer = csv.writer(open(fileNameOut, 'w'), lineterminator='\n', delimiter=';')
            writer.writerows(new_rows)


convert_csv(['*land.csv', '*near.csv'])