@ECHO OFF
call php -q C:\wamp\www\delverdb\xmlconverter.php oracle > oracle.xml
call php -q C:\wamp\www\delverdb\xmlconverter.php setlist > setlist.xml