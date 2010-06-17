USE DBKodersLog
BULK 
INSERT uidtc
        FROM 'C:\sandbox\log-analysis\LogQuery\sql\data\unt.csv'
            WITH
    (
                FIELDTERMINATOR = ',',
                ROWTERMINATOR = '\n'
    )
GO

