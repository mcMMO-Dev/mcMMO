* git enforced \n line endings
* 4-spaces instead of tabs for indentation
* Spaces after control statements before beginning parenthesis or brace
 * <pre>if (</pre>
 * <pre>for (</pre>
 * <pre>while (</pre>
 * <pre>do {</pre>
 * <pre>try {</pre>
 * <pre>public class SomeClass {</pre>
* Spaces after closing parenthesis in a control statement
* No newline after closing parenthesis and space in a control statement:
 * <pre>if () {</pre>
 * <pre>for () {</pre>
 * <pre>while () {</pre>
 * <pre>catch () {</pre>
 * <pre>switch () {</pre>
* Newlines before an else, else if, catch, and finally statement
 * <pre>}
else {</pre>
 * <pre>}
else if () {</pre>
 * <pre>}
catch () {</pre>
 * <pre>}
finally () {</pre>
* Space after ; in for statement, not before
 * <pre>for (int i = 0; i < 10; i++) {</pre>
* Spaces around operators
 * <pre>a + b</pre>
 * <pre>a == b</pre>
 * <pre>for (Type blah : blahs) {</pre>
 * <pre>"String " + "concatenation"</pre>
* Spaces around { and } on one-line declarations
 * <pre>public boolean aMethod() { return something; }</pre>
* Fewer extraneous parentheses, except when they increase readability
 * <pre>if (a == b || b == c)</pre>
* Indent on case in switch case statements
 * <pre>switch () {
            case 1:</pre>
* Mandatory comment for when a case falls through, and when not stacking
 * <pre>switch () {
            case 1:
                // Fallthrough

            case 2:</pre>
 * <pre>switch () {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                // Fallthrough

            case 6:</pre>
* Empty line between cases after break, return, or fallthrough
 * <pre>switch () {
            case 1:
                break;

            case 2:</pre>
* Whenever possible, check for a negative, rather than a positive
 * <pre>if (!something) {
            // Do things
        }

        // Do other things</pre>
* Java standard class and method naming, with exception to McMMO in a class name
 * <pre>thisIsAMethod()</pre>
 * <pre>ThisIsAClass</pre>
 * Exception:
<pre>McMMOCustomClass</pre>
* No space before opening parenthesis for methods, or constructors
 * <pre>public void thisIsAMethod() {</pre>
 * <pre>public ThisIsAClass() {</pre>
* Spaces after comma in method calls and constructors, not before
 * <pre>something.methodCall(variable, otherVariable);</pre>
* Accessing of variables with this. only when necessary to resolve scope
* Class variables always defined at the top, before the constructor
* No empty line between class declaration and beginning of variable declaration
* Always a empty line at the end of a method or constructor definition
* Constructors come before methods, and are typically ordered from the ones with the least arguments to the most
* Methods should be ordered in this manner:
 * override public
 * public
 * static public
 * abstract public
 * override protected
 * protected
 * static protected
 * abstract protected
 * override private
 * private
 * static private
 * abstract private
* No one-line if statements, they should all have brackets