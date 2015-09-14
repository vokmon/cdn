# cdn
The purpose of this project is to understand the techniques used by the CDN in recdusing the amount of data transmission and practice developing distributed applications.

The system constis of three components

Client  <---->  Proxy server (cache) <----> Server

The server provides a service that allows users to browse and download files. The cache
caches the data previously downloaded by the client. It should be assumed that the three
components reside at different locations. The client program interacts with the server
through the cache. That is, when the client wants to browse the list of files available on
the server or to download a file from the server, the client’s requests are sent to the cache
first and the cache forwards the requests to the server. Similarly, the server also sends the
replies to the clients through the cache.

Part 1
1. The client program should provide an interface that (a) allows the user to display the
list of files that are available on the server, (b) allows the user to select a file from the
list of available files to download, and, (c) allows the user to display the contents of
the downloaded file.

2. The cache should cache the files previously downloaded by the client.
o If the user requests for a file that has the same name as a previously
downloaded file, the cached copy of the file should be returned to the user.
o The cache should keep a log to record of the activities of the cache. For each
user’s file downloading request, the log should indicate whether the request is
satisfied by a cached file or the requested file needs to be downloaded from
the server. For example, when a user’s request is received, the following log
entry might be created:
user request: file xyz at 10:27:00 2015-08-01
response: cached file xyz
or
user request: file xyz at 10:27:00 2015-08-01
response: file xyz downloaded from the server
o The cache should provide an interface that allows the user to view the
contents of the cache’s log and the list of files that are cached on the server.
o The interface should provide a function (e.g. a clear button) that allows the
files in the cache to be cleared (i.e. deleted).
o It should be assumed that the cache has sufficient space to hold all the
downloaded files. That is, you do not need to consider cache replacement
policy.

3. The server program should provide (a) an operation that allows the user to download
a file with a given name, and, (b) an operation that lists the names of the files
available on the server.

Part 2
Modify the implementation of cache and server in part 1 to allow fragments of the files to
be cached. In your implementation, the fragmentation of the files should be carried out
automatically. That is, the users do not need to rewrite the files for caching purpose. The
requirements for the client program are the same as in part 1. The requirements for the
cache are as below:

1. When a user requests for a file, if there are some cached data that can be used to
construct the requested file, the cached data should be used, and, only the data that do
not exist on the cache should be downloaded from the server.

2. The cache should keep a log to record of the activities of the cache. For each user’s
file downloading request, the log should indicate the percentage of the file that is
constructed using the data cached on the server. The percentage is defined as “the size
of the cached data used to construct the file / the size of the file” where the size is
measured in bytes. For example, when a user’s request is received, the following log
entry might be created:
user request: file xyz at 10:27:00 2015-08-01
response: 82% of file xyz was constructed with the cached data

3. The cache should provide an interface that allows the user to view the contents of the
cache’s log and the contents of the data being cached on the server.

4. The interface should provide a function (e.g. a clear button) that allows the files in the
cache to be cleared (i.e. deleted).

5. It should be assumed that the cache has sufficient space.
For the server program, in addition to the requirements specified in part 1, the server
program should also provide operations that are necessary to allow the cache to download
fragments of the files
