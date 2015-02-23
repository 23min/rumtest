# rumtest

React based app constructed via messages (streamed rendering).

The idea is to think of DOM nodes as spreadsheet cells and to use a websocket transport to hook up front-end cells to back-end cells.

Application logic is to be kept in cells, just like with a spreadsheet.

Back-end -> WS -> Front-end input cells -> Formula cells (for logic) -> Rum -> React rendering
DOM Events -> React -> Front-end action cells -> WS -> Back-end

When new values are presented on input cells, the UI updates, if necessary.

"Formula cells"  provide UI logic and present values to Rum which generates React components.

Events generated on the front-end (e.g. mostly user actions)  feed to "action" cells, which send messages to the back-end.

## TODO:

Websocket message layer, dispatch to input cells.
Handling DOM/React events.

## TODO Down The Road:

Build up GUI purely with messages, eg. the cell structure and the React components are generated from messages only and not predeclared.
Independence from rendering implementation, eg. swap out React for Famo.us or a desktop GUI kit. (Probably will switch to freactive or something else by that time).
End-user DSL to build applications with at least spreadsheet like simplicity.

## Run it as:

    java -jar rumtest-uber-0.2.4.jar
    open http://localhost:8080/

## Following configuration options are supported:

    java -jar rumtest-uber-0.2.4.jar --ip 0.0.0.0 --port 8080

## Building from source

    lein do clean, cljsbuild once prod, uberjar
    java -jar target/rumtest-uber.jar &

## Development mode

    lein cljsbuild auto dev &
    lein run --reload true &
    open http://localhost:8080/index_dev.html

## Light Table

    lein cljsbuild auto dev
    In Light Table, connect to project.clj using connect bar
    Get ports for the local ws server and update the ws port in index_dev.html

    Instarepl can evaluate rumtest.server namespace, but couldn't get app.cljs namespace working.



