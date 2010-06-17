USE DBKodersLog
BULK 
INSERT uiddays
        FROM 'C:\sandbox\log-analysis\LogQuery\sql\data\uid-days.csv'
            WITH
    (
                FIELDTERMINATOR = ',',
                ROWTERMINATOR = '\n'
    )
GO

