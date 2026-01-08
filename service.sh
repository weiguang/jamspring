#!/bin/sh

java_cmd="java"
# 开启sentinel dashboard， 开启fastjson的safeMode
ops=" -Dcsp.sentinel.dashboard.server=127.0.0.1:8080 -Dfastjson.parser.safeMode=true  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap_dump.hprof"

process_name=okayjam-web-core-1.0.0-SNAPSHOT.jar

Start() {
  PROCESS=$(ps -ef | grep ${process_name} | grep -v grep | awk '{print $2}' )
  if [ "$PROCESS" != "" ]; then
    echo "${process_name} is running!"
  else
    #运行进程
    echo "Start ${process_name}."
    nohup ${java_cmd} ${ops} -jar ${process_name}
    echo "$(ps -ef | grep ${process_name} | grep -v grep)"
  fi

}

Stop() {
  for ((j = 1; j < 60; j++)); do
    PROCESS=$(ps -ef | grep ${process_name} | grep -v grep | awk '{print $2}')
    if [ "$PROCESS" = "" ]; then
      break
    fi
    for i in $PROCESS; do
      echo "Kill the ${process_name}, process [ $i ], $j times!"
      kill "$i"
    done
    sleep 2

  done
}



case "$1" in
start)
  Start
  ;;
stop)
  Stop
  ;;
restart)
  Stop
  Start
  ;;
*)
  echo "usage $0 start|stop|restart"
  ;;
esac

exit 0
