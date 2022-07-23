# Testing Various Markdown Features

## Link to another page

Example: `[Markdown tests](docs/markdown-test.md)`<br>
[Link to README.md](../README.md)

## Including Images

Size specifications are ignored in Github markdown:<br>
![boats image](img/boats.png)

Use of plain HTLM works OK:<br>
<img src="img/boats.png" width="375">

## Java Syntax Highlighting

```java
ImageProcessor ip = ... // any image
GenericFilter filter = new TschumperleDericheFilter();
try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
    filter.applyTo(ip);
}
```

## Math rendering (does not work in Github Wiki pages!)

When $a \ne 0$, there are two solutions to $(ax^2 + bx + c = 0)$ and they are 
$$x = {-b \pm \sqrt{b^2-4ac} \over 2a}$$
Note that NO space or newline is allowed after `$$` in display mode.

