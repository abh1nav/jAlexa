#Accessing the Alexa API using Java

This simple two class project hits the Alexa Web Information Service (http://aws.amazon.com/awis/) and returns the response for any given domain.

It takes input in the form of an input.txt file (one domain per row, separated by \n) and outputs the XML response for each domain into a results an output.txt file.

The UrlInfo class is modified version of the code sample http://aws.amazon.com/code/AWIS/395) provided by Amazon on their docs site. The modification was made to provide a simple static "get" method that takes the domain name as a string and returns the XML response from the Alexa API.

Import the project into Eclipse and hack away.
