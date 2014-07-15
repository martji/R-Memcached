title ·şÎñÆ÷
set localdir=%~dp0
java -cp bin\*; -Dconf.dir=conf  -Dfile.encoding=gbk -Dsun.net.inetaddr.ttl=0  com.myself.server.MemcachedMain
pause