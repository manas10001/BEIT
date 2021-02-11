/*
 The MIT License

 Copyright (c) 2005 - 2007
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2007)
   3. Bryan Carpenter (2005 - 2007)
   4. Mark Baker (2005 - 2007)

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : ProcessID.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Mon Jan 24 09:44:40 BST 2005
 * Revision     : $Revision: 1.8 $
 * Updated      : $Date: 2005/11/27 19:40:12 $
 */
package xdev;

import java.util.UUID;

/**
 * At <i> xdev </i> level, each MPI process is identified by an instance of
 * this class. The MPJ Device level ( <i> mpjdev </i> maps these ProcessIDs
 * to ranks.
 */
public class ProcessID {

  UUID uuid;
  //int rank;

  protected ProcessID() {
  }

  /**
   * Constructor to create an object of ProcessID
   * @param uuid UUID of the process
   * @param rank Rank of the process
  public ProcessID(UUID uuid, int rank) {
    this.uuid = uuid;
    this.rank = rank;
  }
   */

  /**
   * Constructor to create an object of ProcessID
   * @param uuid UUID of the process
   */
  public ProcessID(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Sets UUID of this ProcessID object
   * @param uuid UUID of the process
   */
  public void uuid(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Sets UUID of this ProcessID object
   */
  public UUID uuid() {
    return this.uuid;
  }

  /**
   * Sets rank of this ProcessID object
   * @param rank Rank of the process
  public void rank(int rank) {
    this.rank = rank;
  }
   */

  /**
   * Gets rank of this ProcessID object
   * @return int Rank of the process
  public int rank() {
    return this.rank;
  }
   */

}

