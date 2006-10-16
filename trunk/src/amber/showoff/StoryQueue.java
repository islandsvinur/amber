//_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
//_/_/
//_/_/  Ambient Earth
//_/_/  Christian Luijten <christian@luijten.org>
//_/_/
//_/_/  Copyright(c)2006 Center for Analysis and Design of Intelligent Agents
//_/_/                   Reykjavik University
//_/_/                   All rights reserved
//_/_/
//_/_/                   http://cadia.ru.is/
//_/_/
//_/_/  Redistribution and use in source and binary forms, with or without
//_/_/  modification, is permitted provided that the following conditions 
//_/_/  are met:
//_/_/
//_/_/  - Redistributions of source code must retain the above copyright notice,
//_/_/    this list of conditions and the following disclaimer.
//_/_/
//_/_/  - Redistributions in binary form must reproduce the above copyright 
//_/_/    notice, this list of conditions and the following disclaimer in the 
//_/_/    documentation and/or other materials provided with the distribution.
//_/_/
//_/_/  - Neither the name of its copyright holders nor the names of its 
//_/_/    contributors may be used to endorse or promote products derived from 
//_/_/    this software without specific prior written permission.
//_/_/
//_/_/  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
//_/_/  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
//_/_/  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
//_/_/  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
//_/_/  OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//_/_/  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
//_/_/  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
//_/_/  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//_/_/  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
//_/_/  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
//_/_/  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//_/_/
//_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/

package amber.showoff;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import amber.common.Story;

public class StoryQueue extends Observable implements List<Story> {
    
    private List<Story> list;
    
    public StoryQueue(){
        list = Collections.synchronizedList(new LinkedList<Story>());
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object arg0) {
        return list.contains(arg0);
    }

    public Iterator<Story> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        return list.toArray(arg0);
    }

    public boolean add(Story arg0) {
        setChanged();
        notifyObservers("add");
        return list.add(arg0);
    }

    public boolean remove(Object arg0) {
        setChanged();
        return list.remove(arg0);
    }

    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

    public boolean addAll(Collection<? extends Story> arg0) {
        setChanged();
        notifyObservers("addAll");
        return list.addAll(arg0);
    }

    public boolean removeAll(Collection<?> arg0) {
        setChanged();
        return list.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0) {
        return list.retainAll(arg0);
    }

    public void clear() {
        setChanged();
        list.clear();
    }

    public boolean addAll(int arg0, Collection<? extends Story> arg1) {
        return list.addAll(arg0, arg1);
    }

    public Story get(int arg0) {
        return list.get(arg0);
    }

    public Story set(int arg0, Story arg1) {
        return list.set(arg0, arg1);
    }

    public void add(int arg0, Story arg1) {
        list.add(arg0, arg1);
        setChanged();
        notifyObservers("add");
    }

    public Story remove(int arg0) {
        Story s = list.remove(arg0);
        setChanged();
        notifyObservers("remove");
        return s;
    }

    public int indexOf(Object arg0) {
        return list.indexOf(arg0);
    }

    public int lastIndexOf(Object arg0) {
        return list.lastIndexOf(arg0);
    }

    public ListIterator<Story> listIterator() {
        return list.listIterator();
    }

    public ListIterator<Story> listIterator(int arg0) {
        return list.listIterator(arg0);
    }

    public List<Story> subList(int arg0, int arg1) {
        return list.subList(arg0, arg1);
    }

}
