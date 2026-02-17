# server
Server component agent communicates with

## ssh
The server will accept all incoming connections from agent user, using the public key to identify agents.
Server will respond with instructions for the agent to perform, or nothing if there are no instructions.
Instructions may include request to run further inventory, open a reverse shell, or allow inbound remote access

## ssh flow
agent (scripts) generate ssh key pair if necessary
agent registers public key with server over https
server assigns agent uuid associated with key
