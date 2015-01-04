# jdocstat
Generates simple statistics from JavaDoc

## Usage
Run `mvn compile assembly:single` to generate a all-in-one runnable jar.

Run the prog with two options:

- `-v [version]`  
The version of the JavaApi to parse. One of `V_1_0_2, V_1_1_8, V_1_2, V_1_3_1, V_1_4_2, V_1_5, V_1_6, V_1_7, V_1_8`
- `-f [file]`  
The overview tree html file from the JavaDocs to parse. This is the `tree.html` file for versions `V_1_0_2` and `V_1_1_8`, and `overview-tree.html` for all later versions.

Example:  
`java -jar ./target/jdocstat-0.0.1-SNAPSHOT-jar-with-dependencies.jar -v V_1_5 -f ~/Downloads/java-api/V_1_5/overview-tree.html`  
Will create this output:  
`1.5, 3548, 31814, 51, 317`,
where  
1.5   = Api-Version  
3354  = # classes  
31814 = # methods  
51    = # deprecated classes  
317   = # deprecated methods  

## All in one run
If you don't have time to fiddle arount, you can also just copy this line in a shell  
`chmod +x all-in-one.sh && ./all-in-one.sh && cat ./api-statistics.csv`
