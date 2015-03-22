# script compile and run the application

# compile server code
gcc server_c.c -o server -lpthread
# compile client code
javac -Xlint Client.java

# run server
gnome-terminal -e ./server
# run client
gnome-terminal -e java Client

