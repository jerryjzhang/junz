/*REGISTER myudf.jar;*/
/* raw_logs = load '$pwd/$logFile' as (line);*/
raw_logs = load '$INPUT' as (line);

/* grep relevant lines */
open_session_logs  = filter raw_logs by (line matches '.*HibernateTransactionManager - Opened new Session.*');
close_session_logs = filter raw_logs by (line matches '.*HibernateTransactionManager - Closing Hibernate Session.*');

/* parse lines with [Performance] */
open_session = FOREACH open_session_logs GENERATE FLATTEN (
	REGEX_EXTRACT_ALL(line,'^([\\d|\\w|-]+).(\\S+) \\[(\\S+), \\S+ \\S+ uri=(.+)\\] (\\S+) (\\S+) - Opened new Session \\[org.hibernate.impl.SessionImpl@([\\d|\\w]+).*')) AS
	(date:chararray, time:chararray, process:chararray, url:chararray, mode:chararray, class:chararray, sessionId:chararray);
open_session = FOREACH open_session GENERATE date, time, url, sessionId;

close_session = FOREACH close_session_logs GENERATE FLATTEN (
	REGEX_EXTRACT_ALL(line,'^([\\d|\\w|-]+).(\\S+) \\[(\\S+), \\S+ \\S+ uri=(.+)\\] (\\S+) (\\S+) - Closing Hibernate Session \\[org.hibernate.impl.SessionImpl@([\\d|\\w]+).*')) AS
	(date:chararray, time:chararray, process:chararray, url:chararray, mode:chararray, class:chararray, sessionId:chararray);
close_session = FOREACH close_session GENERATE date, time, url, sessionId;

child = JOIN open_session BY ($0,$2,$3), close_session BY ($0,$2,$3);

child = FOREACH child GENERATE open_session::date, open_session::time, close_session::time, open_session::sessionId, open_session::url;


child = FOREACH child GENERATE FLATTEN(myudf.JunzAlcazarUdf($0,$1,$2,$3,$4)) AS
 	(start_date:chararray, start_time:chararray, duration:int, sessionId:chararray, url:chararray);
child = FOREACH child GENERATE start_date, start_time, duration, url;

store child into '$OUTPUT';
