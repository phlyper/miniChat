# script compile and run the application

# compile server code
gcc server_c.c -o server -lpthread
# compile client code
javac -Xlint Client.java

# run server
gnome-terminal --tab --title="Server Chat" --tab-with-profile=Default --command="./server"
# run client
gnome-terminal --tab --title="Client Chat" --tab-with-profile=Default --command="java Client"

# manual of gnome-terminal
# http://manpages.ubuntu.com/manpages/precise/en/man1/gnome-terminal.1.html

