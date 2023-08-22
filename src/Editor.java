/*

Author: Angus Webb
Created: February 11th, 2022
Class description: "Editor" takes in an ASCIIDataFile and allows the user to edit the text through the console.
 */


import BasicIO.*;
import java.util.*;
public class Editor {
    ASCIIDataFile inputFile;
    ASCIIOutputFile outputFile;
    Node head;
    Node tail;
    static int count = 0; //keeps track of amount of nodes, including the head/tail
    int selectedLine = 0; //the line currently being selected
    String clipboard; //stores the currently copied/cut text
    public Editor(){
        inputFile = new ASCIIDataFile();

        Scanner input=new Scanner(System.in); //takes in user input

        createList(inputFile); //creates a linked List with lines of text from the input file
        help(); //display the input options at the beginning

        while (true){ //user will be able to use as many inputs as they'd like...
            pointAt(selectedLine);
            if (selector(input.nextLine()).equals("q")){
                break; //...until they select "q" which breaks loop
            }
        }
        System.out.println("Toodles!");
    }
    public String selector(String input){ //Selector method:does a command on the text based on the input passed
        switch (input){
            case "p": //print
                System.out.println("==============");
                print();
                if (selectedLine > tail.counter){
                    System.out.println(">"); //indicates that you're off-list
                }
                System.out.println("==============");
                return "p";
            case "h": // display help menu
                help();
                return "h";
            case "u": //move up
                if (selectedLine == 0){ //can't move up when you're already at the top
                    break;
                } else {
                    selectedLine--;
                    return "u";
                }
            case "d": //move down
                if (selectedLine <= tail.counter){ //conditional statement does not go down past off-list
                    selectedLine++;
                }
                return "d";
            case "r": //reload file
                reload();
                selectedLine = 0;
                return "r";
            case "t": //go to top
                selectedLine = 0;
                return "t";
            case "x": //cut text
                cut();
                return "x";
            case "v": //paste text
                insert(clipboard, selectedLine);
                return "v";
                //break;
            case "b": //go to bottom
                selectedLine = tail.counter+1; //+1 accounts for the 0th index
                return "b";
            case "s": //save text into output file
                save(outputFile);
                return "s";
            case "c": //prints out the current line
                getCurrentLine();
                return "c";
            case "q": //exits loop
                return "q";
        }
        if (input.startsWith("i")){ //insert a line
            insert(">"+ input.substring(1), selectedLine);
            sortOrder(); //change sorting of node counters
            pointAt(selectedLine);
            return "i";
        }
        if (input.startsWith("e")){ //edits a line
            if (input.substring(1).isEmpty()){
                deleteNode(selectedLine);
            } else {
                editLine(input.substring(1));
            }
        }
        if (input.startsWith("?")){ //searches for a word
            search(input.substring(1));
            return "?";
        }
        return "";
    }
    public void print(){ //prints the linked list
        Node current = head;
        while (current!=null){ //iterates through list, prints each line
            System.out.println(current.line);
            current = current.next;
        }
    }
    public void insert(String line, int where){ //inserts a node into the linked list.
        Node q = null; //follows p
        Node p = head; //p starts at head
        while (p!=null && p.counter < where){ //while p is not null and p's counter is less than insertion key
            q = p; //q points to where p is pointing to
            p = p.next; //p moves forward
        }
        if (q==null){ //if q is null, then either list is empty or there only exists the head
            head = new Node (null, line, p); //create head node
            head.counter = count++; //set head's counter
            if (p!=null){ //if there IS a head,
                p.prev = head; // its previous node becomes the head
            }
        } else { //if the list has more than one node
            q.next = new Node(q, line, p); //q.next points to where p currently is. new Node goes there
            q.next.counter = getCount(); //set new node's counter
            //tail = q.next; //set tail pointer
            if (p==null){ //if p is null, we're at the end
                tail = q.next; //set tail
            } else { //if p is not null, we're not at the end
                p.prev = q.next; //p's previous becomes q.next
            }
        }
        count++; //increase count
        sortOrder(); //sort
    }
    public void help(){ //prints out the help menu
        System.out.println("u: up. d: down. i[text]: insert line. r: reload file. t: top.");
        System.out.println("h: help. x: cut. v: paste. e[new text]: edit line. b: bottom.");
        System.out.println("s: save buffer. c: current. p: print buffer. ?[string]: search for text. q: quit.");
        System.out.println("-------------------------------------------------------------");
    }
    public void sortOrder(){ //sorts the counters that are stored in each node whenever a node is added
        int tempCounter = 0;
        Node current = head;
        while (current!=null){ //loop until current node is null
            current.counter = tempCounter++; //change the node's stored integer
            current = current.next; //next node
        }
    }
    public void createList(ASCIIDataFile file){ //creates a linked list based on the user-selected input file
        int counter = 0; //so that the insert method knows to add each new line after the preceding one
        while (true) {
            String line = "]"+file.readString(); //read in the line
            if (file.isEOF()){
                break;
            }
            insert(line, counter); //add each new line to a new Node at the end of the list
            counter++; //increment counter
        }
    }
    public void pointAt(int selectedLine){ //point at selected line
        this.selectedLine = selectedLine;
        Node current = head; //start at head
        while (current!=null){ //while current node is not null...
            if (current.counter==selectedLine){ //check if the current node's counter equals the line we're pointed at
                current.line = current.line.replace("]",">"); //if so, add a little pointer
            } else {
                current.line = current.line.replace(">", "]"); //if not, make sure the pointer isn't there
            }
            current = current.next;
        }
    }
    public int getCount(){ //returns the amount of nodes currently in the linked list
        return count;
    }
    private void search(String word) { //this method searches each node for the word specified in argument
        Node current = head;
        while (current != null) {
            String words = current.line.replaceAll("[]>,.!?]", " "); //get rid of pointer characters
            String[] wordsInLine = words.split(" "); //create array of words in the current line
            if (current.counter+1 >= selectedLine){ //enter search ONLY if current node is at or beyond the selected line (+1 to account for 0th index)
                for (int i = 0; i < wordsInLine.length; i++) { //search through the words in the current line
                    if (wordsInLine[i].compareToIgnoreCase(word) == 0){
                        pointAt(current.counter); //if the current word matches the word in argument, point at it
                        break;
                    }
                }
            } else { //if the word is either before the selected line or just doesn't exist, go to bottom
                selectedLine = getCount()-1;
            }
            current = current.next; //if not, keep going
        }
    }
    private void editLine(String word){ //edits the current line
        Node current = head;
        while (current!=null){ //traverse through nodes
            if (current.counter == selectedLine){ //if current node matches the one being pointed to...
                current.line = ">" + word; //update the line
            }
            current = current.next;
        }
    }
    private void cut(){ //"cuts" a line aka removes it and copies it to clipboard
        Node current = head;
        while (current!=null){ //iterate through list until it reaches the line that the user is on
            if (current.counter == selectedLine){
                clipboard = current.line; //copies line to clipboard
                current.line = "]"; //remove line
            }
            current = current.next;
        }
    }
    private void getCurrentLine(){ //prints current line to the console
        Node current = head;
        while (current!=null){ //traverse through nodes
            if (current.counter==selectedLine){ //if current node is the one being pointed to...
                current.line = current.line.replace(">", ""); //remove pointers
                System.out.println(current.line); //...print it out
                current.line = "]"+current.line; //add the pointers back
                break;
            }
            current = current.next;
        }
    }
    private void save(ASCIIOutputFile outputFile){ //saves the list to an output file
        outputFile = new ASCIIOutputFile();
        Node current = head;
        while (current!=null){ //iterate through linked list
            current.line = current.line.replaceAll("[]>]", ""); //take out the pointers
            outputFile.writeString(current.line); //print line onto file
            outputFile.newLine();
            current.line = "]"+current.line; //put the pointers back
            current = current.next;
        }
    }
    private void reload(){ //reloads an ASCIIDataFile
        ASCIIDataFile newFile = new ASCIIDataFile(); //make user enter new data file
        head = null; //set head to null. The rest of the current linked list will now disappear
        createList(newFile); //we can create a new list now using the new input file
    }
    private void deleteNode(int where){ //deletes the node at the given position
        Node p = head; //node p starts at head
        while (p !=null && p.counter!=where){ //search list until p is null or when p's counter is at the deletion key
            p = p.next;
        }
        if (p!=null) {
            if (p.prev == null){ //if p is the head, then head becomes the next node after p, skipping p
                head = p.next;
            } else {
                p.prev.next = p.next; //p.prev's "next" pointer skips p and points to p.next
            }
            if (p.next == null){
                tail = p.prev; //set the tail
            } else {
                p.next.prev = p.prev; //p.next's "prev" pointer skips p and points to p.prev
            }
        }
    }
    public static void main(String[] args) {
        Editor e = new Editor();
    }
}
