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


def processFiles(notify) { 
    def names = new File('.').list() 
    names.eachWithIndex { name, i -> 
        notify(i * 10 / names.size(), name) 
        sleep 50   
    } 
} 
processFiles { filled, info -> 
    print '\b' * 61 
    print '|'*filled + '-'*(10-filled) +' '+ info.padRight(50) 
}