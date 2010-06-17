/*
 * koders-loganalysis: Tools/Scripts to analyze Koders usage log. 
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package bdb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredKeySet;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredMap;

/**
 * SampleViews defines the data bindings and collection views for the sample
 * database.
 *
 */
public class View {

    private StoredMap<Integer, Query> sampleActivitiesMap;
    
    /**
     * Create the data bindings and collection views.
     */
    public View(BdbDatabase db) {

        // In this sample, the stored key and data entries are used directly
        // rather than mapping them to separate objects. Therefore, no binding
        // classes are defined here and the SerialBinding class is used.
        //
        ClassCatalog catalog = db.getClassCatalog();
        EntryBinding<Integer> sampleActivitiesKeyBinding =
            new SerialBinding(catalog, Integer.class);
        EntryBinding<Query> sampleActivitiesDataBinding =
            new SerialBinding(catalog, Query.class);
        
        // Create map views for all stores and indices.
        // StoredSortedMap is not used since the stores and indices are
        // ordered by serialized key objects, which do not provide a very
        // useful ordering.
        //
        sampleActivitiesMap =
            new StoredMap<Integer, Query>(db.getSampleActivitiesDatabase(),
                          sampleActivitiesKeyBinding, sampleActivitiesDataBinding, true);
        
    }

    // The views returned below can be accessed using the java.util.Map or
    // java.util.Set interfaces, or using the StoredMap and StoredEntrySet
    // classes, which provide additional methods.  The entry sets could be
    // obtained directly from the Map.entrySet() method, but convenience
    // methods are provided here to return them in order to avoid down-casting
    // elsewhere.

    public final StoredMap<Integer, Query> getSampleActivitiesMap() {

        return sampleActivitiesMap;
    }

    
    public final StoredEntrySet<Query> getSampleActivitiesEntrySet() {

        return (StoredEntrySet<Query>) sampleActivitiesMap.entrySet();
    }

    public final StoredKeySet<Integer> getSampleActivitiesKeySet() {

        return (StoredKeySet<Integer>) sampleActivitiesMap.keySet();
    }
}
