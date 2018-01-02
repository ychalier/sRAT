# sRAT

A small remote access trojan, for research purposes.

## todo list

1. communication DONE
    1. server host DONE
    2. request syntax DONE
2. features
    1. command exec DONE
    2. data transfer
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

An example showing how to execute an `ipconfig` on a remote machine, using server commands `list`, `select` and `exec`.

	Server hosted on 192.168.1.19:80
	>list
	id     MAC address
	4250   20-16-D8-D3-4F-D8
	>select 4250
	Selected client 4250
	4250>exec ipconfig
	Added command to stack
	4250>EXEC_OUT 4250 
	Configuration IP de Windows
	...