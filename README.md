# Welcome to Odd One Out!

*A game of deception, misdirection, and deduction.*

<img src="https://github.com/oddoneoutgame/OddOneOut/assets/45648517/a9050c7c-9348-4f6e-9d0c-1233a2468caa" alt="drawing" width="1000"/>

[Click here to download](https://play.google.com/store/apps/details?id=com.dangerfield.spyfall.free)


## Description
Odd One Out is a multiplayer game in which one person is assigned to be the odd one out while all other players are assigned a role at a location. The Odd one Out's objective is to figure out the location through a series of well-worded questions while not giving away that he or she is the Odd One Out. Meanwhile, everyone else's objective is to figure out the Odd One Out. All players take turns asking questions and having fun!



## Rules

- As the game starts, every player gets the secret word.
<p align="center">
<img src="https://github.com/oddoneoutgame/OddOneOut/assets/45648517/b0c05918-a33f-41ac-8dc5-b52e63afd541" alt="drawing" style="height:150px;"/>
</p>

- One player doesn’t get to see the secret location. They are the Odd One Out.
- Everyone takes turns picking someone to ask a question to about the secret location and their role there.
- The odd one out is trying to figure out the location without revealing that they are the odd one out.
- The players are trying to figure out who the odd one out is without revealing the secret location.

# How to build

```
The build will fail unless:
1. You have installed the git hooks using `./scripts/install-git-hooks.sh` (sorry, cant have any smelly stinky code getting in)
2. You have the secret files that give you access to the debug and release firebases
```

Please reach out if youd like to receive access to the secret files to build the app. 


## Architecture

The architecture of this application aims to follow recommendations outlined in the [Guide To App Architecture](https://developer.android.com/topic/architecture) by: 
- ensuring unidirectional dataflow via [SEAViewModel](https://github.com/oddoneoutgame/OddOneOut/blob/main/libraries/flowroutines/src/main/java/com.dangerfield.libraries.coreflowroutines/SEAViewModel.kt)
- maintaining an immutable state
- maintaining a clear separation of concerns between components
- using lifecycle-aware state collection
- leveraging dependency injection with Hilt

The view level architecture aims to follow a loose MVI structure without bloat code (reducers, side effect handlers, and stores). So really its just MVVM with actions to state UDF. 

## Tech stack
- Compose 
- Firebase 
- Coroutines/flow (flowroutines) 
- Datastore 
- Timber 
- Github actions - CI/CD
- Hilt - Dependency injection

## Modularization

The modularization followed in this code base aims to encourage low coupling and high cohesion as outlined in the [Guide To App Architecture](https://developer.android.com/topic/modularization)

The code base is separated into 3 module types: `library`, `feature` and `app`. The app module acts as the glue, depending on all modules. 

All Feature and Library modules aim to expose as little as possible. Libraries and features all contain a submodule `impl` where the actual beef lives (Arbys included). Doing this keeps a neat separation of concerns and helps gradle do its job. 

```
NOTE: Other than the app module, no modules should depend on anothers **impl** module. And **common** should depend on basically nothing if possible. 
```


Additionally, I leverage a `build-logic` included build with convention plugins and convenience extensions to make the gradle setup easier.

## CI/CD

This project includes a basic yet opinionated CI/CD system leveraging Github Actions.
On every PR we check:

- **build** - ensures the app isnt broken
- **style** - static code analysis that keeps that smelly code out
- **test** - runs all tests to make sure things are smoother than a fresh jar of skippy

The workflows for these can be found [here](https://github.com/oddoneoutgame/OddOneOut/blob/main/.github/workflows)

Github actions are leveraged for `click releases`. Meaning in order to release a new version all one needs to do is click buttons. The release train includes creating a new release branch with a PR into main, after tests pass a new release draft is created. Publishing that draft uploads the build to the playstore where notes can be added before publishing. 


