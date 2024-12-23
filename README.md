## Run application

- Application can be invoked from the command line after compiling. bin folder holds the runnable `CalculateDirectorySizes.class`
- Take in command line args, flags for setting behaviours and a space seperated list of absolute paths of directories

## Assumptions and Notes

- Flags for the program are -r or -recursive, and -h or -human and are case sensitive
- The directory path on the command line args does not like spaces in the path unless every arg is wrapped in quotes. If no space in path, all quotes can be dropped
- Bad command: `java CalculateDirectorySizes -r D:\directory_testing\books\make books` because of space in path
- Runnable command: `java CalculateDirectorySizes "-r" "D:\directory_testing\books\make books"` every command double quoted
- Example with no spaces: `java CalculateDirectorySizes -r D:\directory_testing\books\makebooks` no space in path, so quotes can be dropped

- Application should work with any major OS / File system, but it was only tested against a windows machine.
