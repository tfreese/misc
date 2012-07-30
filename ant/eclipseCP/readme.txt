Thomas Freese

Eclipse Classpath Task
----------------------

This custom Ant task takes an Eclipse .classpath file and creates
an Ant path-like structure out of it so that you can compile
using Ant or Eclipse, while maintaining the classpath ONLY inside
Eclipse. I wrote this because I like to build my project both ways
and it was getting to be a real pain keeping the two in sync. This
is a one-way extraction: it only goes from Eclipse to Ant, not the 
other way around.

To use it, you need the eclipseclasspathtask.jar file.
The easiest way to install this task is to drop the jar in your
ANT_HOME/lib directory. 

On any project where you wish to use the task, you need to add this XML
snippet

<taskdef resource="de/freese/ant/task/eclipseclasspath.properties"/>

If you put the jar somewhere else, you'll need to set a classpath
(funny, isn't it?) to eclipseclasspathtask.jar and reference it
on your taskdef line.

Once you've taskdef'ed it, it's ready to go. It has several options,
all of which are optional. If you happen to use Eclipse 3.0 M8 or 
later, keep your workspace under C:/Eclipse/.workspace and want 
the classpath structure to be called "classpath", then you can
simply add this line

<eclipsecp/>

somewhere in your build file. If you'd like more control, here is
a list of options and their defaults:

Option                | Required          | Default
---------------------------------------------------------------------
workspace             | yes               |
pathid                | yes               | 
dir                   | No                | The currect directory
filename              | No                | .classpath
verbose               | No                | false

So, a fully specified use would be

<eclipsecp pathid="myclasspath" workspace="/home/me/workspace"
	dir="." filename=".classpath" verbose="true"/>