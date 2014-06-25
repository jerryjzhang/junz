#!/bin/sh
export main_class="org.junz.tas.client.TasClient"

# get the path of this app
app_path=`cd "$(dirname "$0")"; pwd`

echo "app_path=" $app_path

# auto format the jars in classpath
export lib_jars=`ls $app_path/lib/ | grep jar | awk -v apppath=$app_path 'BEGIN{jars="";}{jars=sprintf("%s:%s/lib/%s", jars, apppath, $1);} END{print jars}'`

run_cmd="java -cp $app_path/classes:${lib_jars} ${main_class}"

echo "start command: $run_cmd"
echo "start..."
$run_cmd  2> $app_path/error.log
