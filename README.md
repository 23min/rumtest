# rumtest

The idea is to think of DOM nodes as spreadsheet cells and to use a websocket transport to hook up front-end cells to back-end cells.

Application logic is to be kept in cells, just like with a spreadsheet.

Conceptually, each front-end "input" cell (which receives values from the back-end) is hooked up to a cell on the back-end. 

"Formula cells" are provide UI logic and present values to Rum which generates React components.

When new values are presented on input cells, the UI updates, if necessary.

Events generated on the front-end (e.g. mostly user actions) also feed to cells, to which back-end cells link. 

TODO:

Communication logic (data) is handled in a centralized manner. All data is exchanged via messages targeting (a) specific cell(s).
Components will not have any data push/pull functionality other than their implicit handling via their connection with cells.

TODO Down The Road:

Build up GUI purely with messages, eg. the cell structure and the React components are generated from messages only and not predefined.
Independence from rendering implementation, eg. swap out React for Famo.us or a desktop GUI kit. (Probably will switch to freactive or something else by that time).
End-user DSL to build applications with at least spreadsheet like simplicity.



