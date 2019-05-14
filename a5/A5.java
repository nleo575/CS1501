import java.io.*;
import java.util.*;

/**
	A class to simulate an airline information system.
	@author Nicolas Leo
*/
public  class A5 
{
	// Provides an alphabetically sorted list of cities in the graph
	private static ArrayList<String> citiesSorted = null;

	// Boolean flags are set if graph edges are added, removed, or updated
	// after the program is first started
	private static boolean modified[] = new boolean[3]; 
	private static int len = 0;	// Length of some Stringbuilder text
	private static Graph map, 	// Map read in from a file
					     MST;	// Minimum spanning tree
    // Maps cities to their vertex number provided by the file
	private static LinkedHashMap<String, Integer> cities;
	private static String cities2 [], // Find city name from the vertex number
						  directRoutes = null, 	// Prompt
						  fileName = null,		// Prompt
						  mainMenu = null,		// Prompt
						  mstHeading = null,	// Prompt
						  mstOut = null,		// MST output
						  pathsMenu = null,		// Prompt
						  selectCities = null,	// List of cities
						  strPath = null,		// Formatted output of a path
						  input = null; 		// Hold input from StdIn

	//	Scanner object to read input.
	private static Scanner sc = new Scanner(System.in); 
	// StringBuilder object to generate formatted output.
	private static StringBuilder sb = new StringBuilder();

	/**
		Main method to drive the program.
		@param args String array containing any param line arguments. All
		Arguments are ignored in this program. Interaction is handled with 
		menus and prompts only.
	*/
	public static void main (String[] args)
	{
		int choice = 0; 	// Holds the user's choice of action
		File file = null;	// Data file where flight paths are stored

		//Opening program greeting.
		System.out.print("\033[H\033[2J");  // Clear the console window
		sb.append("Welcome to ACME Airlines\n");
		len = sb.length() -1;
		for(int j = 0; j<len; j++) sb.append("-");
		System.out.printf("%s\n\n",sb);
		sb.setLength(0);

		// Prompt the user for the input file. 
		System.out.print("Enter the name of the input file: ");
		int temp = 0;
		while(true)
		{
			file = new File(fileName =sc.nextLine());
			if(file == null || !file.exists() || !file.isFile())
			{
				if(temp == 0)
				{
					System.out.printf("\nUnable to locate the file. %s%s", 
					"Please try again.\nEnter the name of ",
					"the input file: \0337");

					temp = 1;				
				}
				else 	System.out.print("\0338\033[K");
			}
			else
			{
				try
				{
					sb.append("\nINPUT FILE: ");sb.append(fileName);
					len = sb.length();
					sb.append("\n");
					for(int j = 0; j<len; j++) sb.append("-");
					System.out.printf("%s\n\n",sb);
					sb.setLength(0);
					map = readGraph(file);
					break;
				}
				catch(Exception e)
				{
					System.out.printf("This file does not contain %s%s",
						"valid flight paths. Please try again.\n",
						"Enter the name of the input file: \0337");
				}
			}
		}
		
		/* 	Generate the menus that will be used throughout the program
			and store them in strings to prevent wasting time recreating them
			each time they are needed */

		// Create main menu options
		sb.append("Main Menu\n");
		len = sb.length()-1;
		for(int j = 0; j<len; j++) sb.append("-");
		sb.append("\n\nPlease select from the following:\n");
		sb.append("1. List all direct routes, distances, and prices.\n");
		sb.append("2. Display a MST for service routes based on ");
		sb.append("distances.\n3. Perform a \"shortest path\" search.\n");
		sb.append("4. Locate all trips with a maximum cost.\n");
		sb.append("5. Add a new route between existing cities");
		sb.append(" to the schedule.\n");
		sb.append("6. Remove a route from the schedule.\n");
		sb.append("7. Quit the program.\n\nChoice: \0337");
		mainMenu = sb.toString();
		sb.setLength(0);

		// Create MST output heading
		sb.append("\033[H\033[2JMINIMUM SPANNING TREE\n");
		len = sb.length()-8;
		for(int j = 0; j<len; j++) sb.append("-");
		sb.append("\nThe edges in the MST based on distance follow:\n\n");
		mstHeading = sb.toString();
		sb.setLength(0);

		// Create shortest paths menu options.
		sb.append("\033[H\033[2JPlease select from the following:\n");
		sb.append("1. Find the shortest distance path between two cities.\n");
		sb.append("2. Find the lowest cost path between two cities.\n");
		sb.append("3. Find the path with the fewest hops between two ");
		sb.append("cities.\n4. Return to the main menu.\n\nChoice: \0337");
		pathsMenu = sb.toString();
		sb.setLength(0);

		// Create select cities prompt.
		sb.append("Please select two cities from the following:\n");
		for (String st :citiesSorted) 
		{	sb.append(st); sb.append("\n");}
		sb.append("\nDeparting City: \0337");
		selectCities = sb.toString();
		sb.setLength(0);

		// Main program loop
		while(choice < 7)
		{
			System.out.print(mainMenu);
			while (true) 
			{
				try 
				{
					choice = sc.nextInt();
					if (choice >0 && choice <8) break;
					else 
					{	System.out.print("\0338\033[K"); sc.nextLine();}
				}
				catch (Exception e) 
				{	System.out.print("\0338\033[K"); sc.nextLine();}
			}
				
			switch (choice) 
			{
				case 1: System.out.print("\033[H\033[2J");
						map.updateDirectRoutes();
						System.out.println(directRoutes);
						break;
				case 2: System.out.print(mstHeading);
						findMST();
						System.out.println(mstOut);
						break;
				case 3: System.out.print(pathsMenu);
						while (true) 
						{
							try 
							{
								choice = sc.nextInt();
								if (choice >0 && choice <4) 
								{
									System.out.print("\033[H\033[2J");
									apsp(choice); break;
								}
								else if(choice == 4) break;
								else
								{ 	System.out.print("\0338\033[K");
									sc.nextLine();
								}
							}
							catch (Exception e) 
							{
								System.out.print("\0338\033[K");
								sc.nextLine();
							}
						}
						break;							
				case 4: findCappedPaths(); break;
				case 5:	System.out.print("\033[H\033[2J"); 
						addRoute(); break;
				case 6:	System.out.print("\033[H\033[2J"); 
						removeRoute(); break;
			}
		}
		// End of program loop. The user selected option 7 to exit.
		// If path updated or deleted, save the data to file in a safe manner
		if(modified[2]) 
		{
			System.out.println("Saving changes to disk.");
			try
			{
				//Opens the specified file containing a graph.
				File originalFile = new File(fileName); 

				//Creates a new temporary file in which to write the updated 
				// data to.
				sb.append("_"); sb.append(fileName);
				File tempFile = new File(sb.toString());
				sb.setLength(0);

				//Creates a PrintWriter object to write to the temp file.
				PrintWriter	pw = new PrintWriter(tempFile); 

				//Print the string representation of the map to the tile
				pw.print(map.toString());
				
				pw.close();

				originalFile.delete();
				tempFile.renameTo(new File(fileName));
				System.out.println("Changes successfully saved. Exiting.");
			}
			catch (IOException ex)
			{
				System.out.println("There was a problem saving the file.");
			}
		}
		System.exit(0);	
	}

	/**
		Add a new route between two existing cities, or gives the option
		to update the distance and or cost of an existing route. 
	*/
	public static void addRoute()
	{
		int dep = 0,// Departing city #. 
			arrv = 0, // Arriving city #. 
			dist = 0; // Distance between the two cities
		double cost = 0.0; // Cost of the flight
		Integer t; // Will hold city numbers

		map.updateDirectRoutes(); // Update routes string if necessary
		System.out.printf("Add a new route or update an %s%s\n%s", 
			"existing route.\n", directRoutes, selectCities);

		sc.nextLine(); // consume new line character from menu selection

		while (true) // Get & verify the departing city 
		{
			try 
			{
				input = sc.nextLine();
				if((t = cities.get(input))!= null)
				{ 	dep = t; break; }
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}	

		t=null;
		System.out.printf("Destination City (cannot be %s): \0337", input);
		while (true) // Get & verify the destination
		{
			try 
			{
				input = sc.nextLine();
				if((t = cities.get(input))!= null && t.intValue() != dep)
				{	arrv = t; break; }
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}

		System.out.print("Flight distance (miles): \0337");
		while (true) // Get & verify flight distance
		{
			try 
			{
				dist = sc.nextInt();
				if(dist > 0) break;
				else {System.out.print("\0338\033[K"); sc.nextLine();}
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K"); sc.nextLine();}
		}	

		System.out.print("Flight cost: $\0337");
		while (true) // Get & verify the flight cost
		{
			try 
			{
				cost = sc.nextDouble();
				if(cost>-1.0) break;
				else {System.out.print("\0338\033[K"); sc.nextLine();}
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K"); sc.nextLine();}
		}				
		System.out.println();	sc.nextLine(); // Consume extra new line char

		if(map.getEdge(dep, arrv) != null) // Check if the flight path exists
		{	// If it exists, ask if the user wants to update the cost
			System.out.printf("That route already exists. Would %s",
				"you like to update the distance of the flight? (Y/N): ");

			input = sc.nextLine();
			switch (input.charAt(0)) 
			{
				case 'y': case 'Y': 

				map.getEdge(dep, arrv).dist = dist;
				map.getEdge(arrv, dep).dist = dist;
				System.out.println("Flight distance successfully updated.\n");

				int max = modified.length;
				for(int i = 0; i< max; i++) modified[i]=true;	break;

				default: System.out.println("Flight distance unchanged.\n");
						 dist = map.getDist(arrv, dep);
			}

			System.out.printf("Would you like to update the cost of %s",
								"the flight? (Y/N): ");

			input = sc.nextLine();
			switch (input.charAt(0)) 
			{
				case 'y': case 'Y': 

				map.getEdge(dep, arrv).cost = cost;
				map.getEdge(arrv, dep).cost = cost;
				System.out.println("Flight distance successfully updated.\n");
				if (!modified[0]) 
				{
					int max = modified.length;
					for(int i = 0; i< max; i++) modified[i]=true;
				}
				break;

				default: System.out.println("Flight cost unchanged.\n");
						 cost = map.getCost(arrv, dep);
			}

			if (modified[0]) 
				System.out.printf("Updated route: %s%s%d%s%s%s%,.2f\n\n", 
				cities2[dep], " <--(",dist, " mi)--> ", cities2[arrv], 
				", cost: $", cost);
			else System.out.println("Route unchanged.\n");
		}
		else // Add the new flight path to the map
		{
			map.addEdge(dep, arrv, dist, cost);
			System.out.printf("%s%s%s%d%s%s%s%,.2f\n\n", 
				"Route successfully added.\nNew route: ", cities2[dep], 
				" <--(",dist, " mi)--> ", cities2[arrv], ", cost: $", cost);

			// Set the modified flags so that flight paths, MST, and 
			// updated data will be saved when the program closes
			int max = modified.length;
			for(int i = 0; i< max; i++) modified[i]=true;
		}
	}

	/**
		Helper function to calculate all possible shortest paths from a 
		given city.
		@param type Type of path search to perform. 1=Shortest path by 
		distance, 2=shortest path by cost, 3=shortest path by hops
	*/
	public static void apsp(int type)
	{
		int dep = 0,// Departing city #. 
			arrv = 0; // Arriving city #. 
		Integer t; // Will hold city numbers

		System.out.print(selectCities);
		while (true) // Read & verify departure city
		{
			try 
			{
				input = sc.nextLine();

				if((t = cities.get(input))!= null)
				{	dep = t; t=null; break;}
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}	

		System.out.print("Destination City: \0337");
		while (true) // Read & verify destination/arrival city
		{
			try 
			{
				input = sc.nextLine();
				if((t = cities.get(input))!= null)
				{	arrv = t; System.out.println(""); break; }
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}	

		LinkedList<Integer> shortestPaths[] = findAPSP(dep, type);
		if (shortestPaths[arrv].peek() == dep)
		{
			switch (type) 
			{
				case 1: sb.append("SHORTEST DISTANCE PATH from "); break;
				case 2: sb.append("LOWEST COST PATH from "); break;
				case 3: sb.append("FEWEST HOPS from ");
			}
			sb.append(cities2[dep]);sb.append(" to ");
			sb.append(cities2[arrv]);
			len = sb.length(); sb.append("\n");
			for(int j = 0; j<len; j++)	sb.append("-");
			System.out.println(sb);
			sb.setLength(0);

			switch (type) 
			{
				case 1: 
				System.out.printf("Total distance: %,d mi\n%s%s\n\n",
					findPathLength(shortestPaths[arrv], type), 
					"Path with edges:\n", strPath);	break;

				case 2: 
				System.out.printf("Total cost: $%,.2f\n%s%s\n\n",
					findPathLengthD(shortestPaths[arrv]),
					"Path with edges:\n", strPath);	break;

				case 3: 
				System.out.printf("Total hops: %,d\n%s%s\n\n",
					findPathLength(shortestPaths[arrv], type),
					"Path:\n", strPath);
			}	
		}
		else
			System.out.printf("There is no path from %s to %s%s", 
				cities2[dep], cities2[arrv],
			 "\nPlease check with other regional airlines.\n\n");
	}

	/**
		Returns all possible shortest paths from the start vertex using
		Dijkstra's shortest path algorithm. Each index in the result 
		represents the city at the beginning of the path. Each entry is a
		string that represents the shortest path to a given destination city.
		The array space is null if no path exists to the endpoint vertex.
		@param city The starting city
		@param type The type of shortest path to find. 1=by distance, 
		2=by cost, 3=by hops
		@return All possible shortest paths from the starting city, or null
		if no paths exist.
	*/
	public static LinkedList<Integer>[] findAPSP(int city, int type)
	{
		int V = map.V(), dist, newDist, hops, newHops, curr, i, visited = 0;
		double cost, newCost;		
		//Check if start vertex even exists in the graph
		if (city > V) return null;

		//algorithm and path storage
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] result = new LinkedList[V];
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] paths = new LinkedList[V];

		Edge [] edges = new Edge[V];
		edges[city] = new Edge(city, city, 0, 0, 0.0);

		Comparator<Edge> comp = null;
		switch (type) 
		{
			case 1: comp = new ByDist(); break; // Shortest path by distance
			case 2: comp = new ByCost(); break; // Shortest path by cost
			case 3: comp = new ByHops(); 		// Shortest path by hops
		}
		PriorityQueue<Edge> PQ = new PriorityQueue<Edge>(comp);
		PQ.add(edges[city]);

		// Continue until all vertices are visited
		while(PQ.peek() != null || visited < V) 
		{
			if(PQ.peek() == null) // If the graph isn't connected
			{
				// Find next connected subtree of this graph
				int count = 0;
				while(true)
				{
					if(edges[count] == null) 
					{
						edges[count] = new Edge(count, count, 0, 0, 0.0);
						curr = count; break;
					}
					count++;
				}
			}
			else
				curr = PQ.poll().vertex;

			visited++;

			// Examine all of the current vertex's neighbors, add to PQ
			// if a neighbor has never been seen before.
			LinkedHashMap<Integer,Edge> vertex = map.getAdjLst(curr);
			for (Map.Entry<Integer, Edge> edge : vertex.entrySet())
			{
				i = edge.getValue().vertex;
				dist = edge.getValue().dist;
				cost = edge.getValue().cost;
				if(dist > 0)
				{
					if(edges[i] == null)
					{
						edges[i] = new Edge(i, curr, dist + edges[curr].dist,
								1+ edges[curr].hops, cost + edges[curr].cost);
						PQ.add(edges[i]);
					}
					else // Vertex already encountered
					{
						switch (type) 
						{
							case 1: // If SP by distance
								newDist = dist + edges[curr].dist;

								if (newDist < edges[i].dist) 
								{
									edges[i].via = curr;
									edges[i].dist = newDist;
									if(PQ.remove(edges[i])) PQ.add(edges[i]);
								}
								break;
							case 2: // If SP by cost
								newCost = cost + edges[curr].cost;

								if (newCost < edges[i].cost) 
								{
									edges[i].via = curr;
									edges[i].cost = newCost;
									if(PQ.remove(edges[i])) PQ.add(edges[i]);
								}
								break;
							case 3: // If SP by hops
								newHops = 1 + edges[curr].hops;

								if (newHops < edges[i].hops) 
								{
									edges[i].via = curr;
									edges[i].hops = newHops;
									if(PQ.remove(edges[i])) PQ.add(edges[i]);
								}
						}
					}
				}
			}					
		}

		//return all shortest paths
		int j;

		for (i = 0; i < V; i++)
		{
			j = i;
			// Via gives reverse order
			paths[i] = new LinkedList<Integer>();
			while(true)
			{
				paths[i].add(new Integer(j));
				if (j == edges[j].via) break;
				j = edges[j].via;
			}

			// Final result needs to be reversed & put into the correct order
			result[i] = new LinkedList<Integer>();
			while(paths[i].peekLast()!=null)
				result[i].add(paths[i].removeLast());
		}

		return result;
	}

	/**
		This method performs a depth-first search of a graph (flight path map)
		to finds all possible paths that are capped by a given dollar amount 
		provided by the user. If no paths are available for the given amount, 
		the user will be notified.
	*/
	public static void findCappedPaths()
	{
		int V = map.V(), paths = 0;
		boolean visited []= new boolean[V];
		double cc = 0.0, 		// Cumulative cost of the path
			   maxCost = 0.0, 	// Max cost set by the user
			   newCC = 0.0; 	// Used for checking new cumulative cost
		Integer prev = null; 	// Holds the last city visited

        // Use iterator to keep track of which city in each adjacency list
        // needs to be explored next
        @SuppressWarnings("unchecked")
        Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[V];

        // User a stack to do a depth-first search of the flight path map
        LinkedList<Integer> stack = new LinkedList<Integer>();

        // Prompt the user for the maximum fare to search for
		System.out.print("\033[H\033[2JMaximum fare to search for: $\0337");
		while (true) 
		{
			try 
			{
				maxCost = sc.nextDouble();
				if (maxCost <1.0) 
				{	System.out.print("\0338\033[K"); sc.nextLine();}
				break;
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K"); sc.nextLine();}
		}

		sb.append("ALL PATHS OF COST $");
		sb.append(String.format("$%,.2f OR LESS\n",maxCost));
		System.out.print(sb);
		sb.setLength(0);
		sb.append("Note that paths are duplicated, once from each city's ");
		sb.append("point of view\n");
		len = sb.length() -1;
		for(int j = 0; j<len; j++) sb.append("-");
		sb.append("\nList of flights for at most ");
		sb.append(String.format("$%,.2f:\n",maxCost));
		System.out.println(sb);
		sb.setLength(0);

        // Perform DFS starting from each city on the map
        for (int i = 0; i < V; i++)
        {	
        	stack.add(i); // Start DFS from next city
        	visited[i] = true;
        	// Initialize the adjacency iterator for each city
	        for (int v = 0; v < V; v++)
	            adj[v] = map.getAdjLst(v).keySet().iterator();

	        sb.append("Departing from ");sb.append(cities2[i]);
    		len = sb.length() + 1; sb.append(":\n");
			for(int j = 0; j<len; j++) sb.append("-");
			sb.append("\n"); System.out.print(sb);
			sb.setLength(0);

	        while (!stack.isEmpty()) 
	        {	
	        	// Look at the "top" city on the stack
	            int v = stack.peekLast(); 
	            // Check if the city has any remaining connections
	            if (adj[v].hasNext()) 
	            {
	                int w = adj[v].next();
	                if (!visited[w]) 
	                {
	                    // See what the new cumulative cost will be
	                    newCC = cc + map.getCost(stack.peekLast(), w);

						// Visit the city if it's within budget
	                    if(newCC <= maxCost) 
	                    { 	
	                    	paths++;
	                    	visited[w] = true;
	                    	cc = newCC; 
		                    stack.add(w);
		                    System.out.printf("Cost: $%,8.2f, Path: %s\n",
									cc, listToStr(stack));
	                    }
	                }
	            }
	            else
	            {
	            	prev = stack.removeLast(); // "pop" from the stack
	            	if(stack.size() < 2) cc = 0.0;
	            	else cc -= map.getCost(stack.peekLast(), prev);

	            	if(prev!=null) visited[prev] = false; 
	            	// Mark city as unvisited since there may be more than 
	            	// one way to reach this city
	            }
	        }
            if(paths == 0) 
	            System.out.printf("No flight paths found for the %s",
	            	"given price.\n\n");
            else System.out.println();	

            paths = 0;
        }
	}

	/**
		Computes a minimum spanning forest using Prim's algorithm.
	*/
	public static void findMST()
	{
		/*	Only compute a new MST if this is the first time running this
			method, or if the base map of flight paths has been modified
			since this method was last called. */
		if(MST != null && !modified[1]) return;

		modified[1] = false;
		int unvisitedEdges[], 	// Optimization to keep track if a vertex 
								// has more edges to check
			numVisited = 0,		// Number of vertices visited
			sm,					// Smallest dist. among all unvisited vertices
			temp = -1,			// Temporary calculations
			unconnected = 0,	// Total number of connected subtrees
			V = map.V(), 		// Total number of vertices
			vertex = 0;			// Vertex number

		unvisitedEdges = new int[V];
		//Initialize unvisited eges with number of edges for each vertex
		for (int i = 0;i<V ;i++) unvisitedEdges[i] = map.getAdjLst(i).size();

		boolean more = true; // If there are more vertices to visit
		boolean[] visited = new boolean[V]; // Keep track of which vertices
										    // are visited

		Edge tEdge = null, 		// Temporary edge
			 vertexToAdd = null;// 2nd temporary edge

		//Choose random vertex to start
		Random rand = new Random();
		while(temp< 0)	temp = rand.nextInt()%V;

		visited[temp] = true;	// Mark the vertex as visited
		numVisited = 1;			// Increment the number of visited vertex

		// Create a new Graph to represent MST.
		Graph mst = new Graph(V);

		// Algorithm
		while(numVisited < V)
		{
			sm = Integer.MAX_VALUE;
			for (int i = 0; i < V; i++)
				if(visited[i] && unvisitedEdges[i] > 0 &&
					(tEdge = map.leastDist(visited, i))!= null &&
					tEdge.dist < sm)
				{	sm = tEdge.dist; vertex = i; vertexToAdd = tEdge; }

			// Once for loop is completed, mark the new vertex as visited
			// and add the new edge to the MST's adjacency list

			if(vertexToAdd!=null)
			{
				unvisitedEdges[vertex]--; 
				unvisitedEdges[vertexToAdd.vertex]--;

				visited[vertexToAdd.vertex]= true;
				mst.addEdge(vertex, vertexToAdd.vertex, sm, vertexToAdd.cost);
				numVisited++;

				sb.append(cities2[vertex]);
				if(cities2[vertex].length() <8) sb.append("\t");
				sb.append("\t<--> ");
				sb.append(cities2[vertexToAdd.vertex]);
				if(cities2[vertexToAdd.vertex].length() <11) sb.append("\t");
				sb.append(String.format("\tdistance:%,6d mi\n", 
					vertexToAdd.dist));
				vertexToAdd = null;
			}
			else
			{
				int i = 0;
				while(true) 
				{	// Find the next unconnected component
					if(visited[i]==false)
					{	visited[i] = true; break; }
					i++;
				}
				unconnected++; numVisited++;
			}

		}
		if(unconnected>0)
		{
			sb.append("*Note that this graph is not connected.\n");
			sb.append("*It contains ");sb.append(unconnected+1);
			sb.append(" connected subtrees.\n");
		}

		mstOut = sb.toString(); // Save the output string for repeated calls
		sb.setLength(0);
		MST = mst; // Save a reference to the temporary mst
	}

	/**
		Returns the length of the path from the given linked list of 
		vertices representing a path. Also creates a string version of 
		this path for printing and stores the string in the private variable 
		strPath.
		@param path LinkedList of Integer objects where each element of the 
		list represents a vertex (city) and the list represents a path.
		@param type Integer representing the type of path search to perform.
		1=Shortest path by distance, 2=shortest path by cost, 3=shortest path
		by hops
		@return The length of the path or -1 if the path is invalid.
	*/
	public static int findPathLength(LinkedList<Integer> path, int type)
	{
		if(path.size() == 1) { strPath = cities2[path.peek()]; return 0;}

		StringBuilder result = new StringBuilder();
		int total = 0, plen = path.size() - 1, sum=0, max = plen + 1;

		Integer curr, next = null;
		ListIterator<Integer> li = path.listIterator(0),
							  li2 = path.listIterator(1);

		for (int i = 0; i < plen; i++) 
		{
			max--;
			curr = li.next();
			result.append(cities2[curr]);

			next = li2.next();
			if (type == 1) 
			{
				sum = map.getDist(curr, next); 
				if(max > 0)
				{ 
					result.append(" --(");
					result.append(String.format("%,d", sum));
					result.append(" mi)--> ");
				}
				total +=  sum;
			}
			else
			{
				total++;
				if(max > 0) result.append(" --> ");
			}
		}
		result.append(cities2[next]);
		strPath = result.toString();
		return total;
	}

	/**
		This method is identical to findPathLength(), except that it returns
		a double rather than an int representing a path length. This also 
		creates a string representation of this path for printing and stores
		the string in the private variable strPath.
		@param path String representing a path in the graph
		@return -1 if the path is invalid, otherwise the length of the path
	*/
	public static double findPathLengthD(LinkedList<Integer> path)
	{
		if(path.size() == 1){ strPath = cities2[path.peek()]; return 0.0;}

		StringBuilder result = new StringBuilder();
		int plen = path.size() - 1, max = plen + 1;
		double sum, total = 0.0;
		Integer curr, next = null;
		ListIterator<Integer> li = path.listIterator(0),
		 					  li2 = path.listIterator(1);

		for (int i = 0; i < plen; i++) 
		{
			max--;
			curr = li.next();
			result.append(cities2[curr]);

			next = li2.next();
			sum = map.getCost(curr, next);
			
			if(max > 0)
			{ 
				result.append(" --(");
				result.append(String.format("$%.2f", sum));
				result.append(")--> ");
			}

			total +=  sum;
		}
		result.append(cities2[next]);
		strPath = result.toString();
		return total;
	}

	/**
		Creates a formatted string representing a given path.
		@param path A linked list of Integer objects where each integer
		represents a vertex from a graph and the list represents a path. 
		@return The formatted string representing the given path.
	*/
	public static String listToStr(LinkedList<Integer> path)
	{
		if(path.size() == 1) return cities2[path.peek()];

		String result = null;
		int plen = path.size() - 1, max = plen + 1;
		Integer curr, next = null;
		ListIterator<Integer> li = path.listIterator(0),
		 					  li2 = path.listIterator(1);

		for (int i = 0; i < plen; i++) 
		{
			max--;
			curr = li.next();
			sb.append(cities2[curr]);

			next = li2.next();
			
			if(max > 0)
			{ 
				sb.append(" --(");
				sb.append(String.format("$%,.2f", map.getCost(curr, next)));
				sb.append(")--> ");
			}
		}
		sb.append(cities2[next]);
		result = sb.toString();
		sb.setLength(0);
		return result;
	}

	/**
		Reads in a graph from the provided text file.
		@param graphFile File object containing a valid graph
		@throws Exception if the graph is invalid
		@return Graph read in the from file or exception if the file
		doesn't contain a valid graph.
	*/
	static Graph readGraph(File graphFile) throws Exception
	{
		Scanner fscan = null; 
		String line, adjacency[]; 
		int size;
        fscan = new Scanner(graphFile);

		// Read in the size (i.e. # of vertices) of the graph
		if(fscan.hasNextLine()) size = Integer.parseInt(fscan.nextLine());
		else return null;

		Graph g = new Graph(size);
		cities = new  LinkedHashMap<String,Integer>();
		cities2 = new String[size];
		citiesSorted = new ArrayList<String>();
		for (int i = 0; i < size; i++ ) 
		{
			line = fscan.nextLine();
			cities.put(line, new Integer(i));
			cities2[i] = line;
			citiesSorted.add(line);
		}
		Collections.sort(citiesSorted); // Alphabetically sorts the list

		while(fscan.hasNextLine())
		{
			line = fscan.nextLine();
			adjacency = line.split(" ");
			g.addEdge(Integer.parseInt(adjacency[0])-1, 
						 Integer.parseInt(adjacency[1])-1, 
						 Integer.parseInt(adjacency[2]),
						 Double.parseDouble(adjacency[3]));
		}

		fscan.close();
		return g;
	}

	/** Removes a route between two existing cities. */
	public static void removeRoute()
	{
		int dep = 0,// Departing city #. 
			arrv = 0, // Arriving city #. 
			dist = 0; // Distance between the two cities
		double cost = 0.0; // Cost of the flight
		Integer t; // Will hold city numbers

		map.updateDirectRoutes(); // Run in case routes were updated
		System.out.printf("Remove an existing route.\n%s\n%s", directRoutes, 
			selectCities);

		sc.nextLine(); // consume new line character from menu selection
		while (true) // Get & verify the departing city 
		{
			try 
			{
				input = sc.nextLine();
				if((t = cities.get(input))!= null)
				{ 	dep = t;  t=null; break;}
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}	

		System.out.printf("Destination City (cannot be %s): \0337", input);
		while (true) // Get & verify the destination
		{
			try 
			{
				input = sc.nextLine();
				if((t = cities.get(input))!= null && t.intValue() != dep && 
					map.getEdge(dep, t) != null)
				{	arrv = t; break; }
				else System.out.print("\0338\033[K");
			}
			catch (Exception e) 
			{	System.out.print("\0338\033[K");}
		}

		// Confirm the removal
		System.out.printf("\nAre you sure you want to remove %s",
			"the route from the schedule? (Y/N): ");

		input = sc.nextLine();
		switch (input.charAt(0)) 
		{
			case 'y': case 'Y': 
			map.removeEdge(dep, arrv);
			System.out.println("Route successfully removed\n");

			int max = modified.length;
			for(int i = 0; i< max; i++) modified[i]=true;	break;

			default: System.out.println("Route unchanged.\n");
		}
	}

	/**
		Class to represent an undirected edge-weighted graph. Edges are 
		stored in an adjacency list (represented using a linked hash map).
	*/
	static class Graph
	{
		private final int V; 	// total number of vertices
		private int E; 			// Total number of edges
		private LinkedHashMap<Integer,Edge> [] adj;	// Adjacency list

		/**
			Constructor to make a new graph.
			@param v Number of vertices in the graph.
		*/
		public Graph(int v)
		{
			V = v;
			E = 0;
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer,Edge> [] temp = new LinkedHashMap[v];
			adj = temp;
			for (int i = 0;i<v;i++) 
				adj[i] = new LinkedHashMap<Integer,Edge>(8);
		}
		
		/** 
			Number of edges 
			@return number of edges in the graph.
		*/
		public int E()
		{	return E; }

		/** 
			Number of vertices 
			@return number of vertices in the graph.
		*/
		public int V()
		{	return V; }

		/** 
			Adds an edge to this graph 
			@param i Vertex representing the first city
			@param j Vertex representing the second city
			@param d Distance between the two vertices
			@param c Cost of the flight between the two cities
		*/
		public void addEdge(int i, int j, int d, double c)
		{
			adj[i].put(new Integer(j), new Edge(j, d, c));
			adj[j].put(new Integer(i), new Edge(i, d, c));
			E++;
		}

		/**
			Returns the cost of a given flight between two cities.
			@param i Vertex representing the first city
			@param j Vertex representing the second city
			@return Cost of the flight between the two cities 
		*/
		public double getCost(Integer i, Integer j)
		{
			if(i.equals(j) || i == null || j == null) return 0.0;
			Edge temp = adj[i].get(j);
			if (temp != null) return temp.cost;

			return -1.0;
		}

		/**
			Returns the distance between two cities.
			@param i Vertex representing the first city
			@param j Vertex representing the second city
			@return Distance between the two cities
		*/
		public int getDist(Integer i, Integer j)
		{
			if(i.equals(j)) return 0;
			Edge temp = adj[i].get(j);
			if (temp != null) return temp.dist;

			return -1;
		}

		/**
			Returns the edge between a given set of vertices (cities)
			@param i Vertex representing the first city
			@param j Vertex representing the second city
			@return Edge between a given set of vertices
		*/
		public Edge getEdge(Integer i, Integer j)
		{	return adj[i].get(j);}

		/**
			Returns the adjacency list for a given vertex.
			@param i Vertex representing the city
			@return Adjacency list for a given vertex
		*/
		public LinkedHashMap<Integer,Edge> getAdjLst(int i)
		{	return adj[i]; }

		/**
			Find the unvisited neighboring vertex with the shortest distance
			from a given vertex 
			@param visited Array to keep track of which vertices were
			already visited.
			@param v Represents the vertex whose neighbors will be
			searched
			@return The unvisited neighboring vertex with the shortest 
			distance from a given vertex or null if all neighboring vertices
			are already visited
		*/
		public Edge leastDist(boolean [] visited, int v)
		{
			int sm = Integer.MAX_VALUE, max = adj[v].size(), temp = 0;
			Edge tEdge = null, returnEdge = null;
			for (Map.Entry<Integer, Edge> edge : adj[v].entrySet())
				if(!visited[edge.getKey()] &&  
					(tEdge = edge.getValue()).dist < sm)
					{	sm = tEdge.dist; returnEdge = tEdge;	}
				
			return returnEdge;
		}

		/**
			Removes an edge between two vertices and decrements the edge count
			@param i Vertex representing the first city
			@param j Vertex representing the second city
		*/
		public void removeEdge(int i, int j)
		{	adj[i].remove(j);	adj[j].remove(i); E--;}

		/**
			Returns a properly formatted string representing this graph.
			@return Properly formatted string representing this graph.
		*/
		public String toString()
		{
			String ret = null;
			sb.append(V);
			sb.append("\n");
			for(String city: cities.keySet())
			{	sb.append(city); sb.append("\n");}

			for (int i = 0; i < V ; i++ ) 
			{
				for (Map.Entry<Integer, Edge> n : adj[i].entrySet())
				{
					if(n.getValue().vertex > i)
					{
						sb.append(i+1);sb.append(" ");
						sb.append(n.getValue().vertex +1);
						sb.append(" ");sb.append(n.getValue().dist);
						sb.append(" ");sb.append(n.getValue().cost);
						if(i+1<V) sb.append("\n");
					}
				}			
			}
			ret = sb.toString();
			sb.setLength(0);
			return ret;
		}

		/**
			Generates a string representing all direct routes on the map.
			The result is stored in the private static string directRoutes.
		*/
		public void updateDirectRoutes()
		{
		/*	Only compute the direct routes if this is the first time running 
			this method, or if the base map of flight paths has been modified
			since this method was last called. */
			if(directRoutes != null && !modified[0]) return;
			modified[0] = false;
			sb.append("All Direct Routes: ");
			sb.append(E);
			sb.append(" unique routes (Duplicates not shown)");
			len = sb.length();
			sb.append("\n");
			for(int j = 0; j<len; j++) sb.append("-");
			sb.append("\n");
			for (int i = 0; i < V ; i++ ) 
			{
				for (Map.Entry<Integer, Edge> n : adj[i].entrySet())
				{
					if(n.getValue().vertex > i)
					{
						sb.append(cities2[i]);
						if(cities2[i].length() <8) sb.append("\t");
						sb.append("\t<--> ");
						sb.append(cities2[n.getValue().vertex]);
						if(cities2[n.getValue().vertex].length() <11) 
							sb.append("\t");
						sb.append("\tdistance:");
						sb.append(String.format("%,6d", 
							 				n.getValue().dist));
						sb.append(" mi\tcost: ");
						sb.append(String.format("$%,8.2f", 
							n.getValue().cost));
						if(i+1<V) sb.append("\n");
					}
				}			
			}
			directRoutes = sb.toString();
			sb.setLength(0);
		}		
	}

	/** Class used to represent the edges of an undirected edge-weighted 
		graph. */
	static class Edge implements Comparable <Edge>
	{
		int vertex, // Vertex number in the adjacency list
			via,	// Path-via (i.e. previous vertex in the path)
			dist,	// Distance from the other vertex or from the origin city
			hops;	// Number of hops from the starting city along a path
		double cost;// Cost of the flight from the other vertex or 
					//from the origin city

		/** 
			Public constructor
			@param v Integer representing the vertex
			@param d Distance between this vertex and the other
			@param c Cost of the flight between the vertices 
		*/		
		public Edge (int v, int d, double c)
		{	vertex = v; via = v; dist = d; hops = 0; cost = c;}

		/** 
			Public constructor
			@param v Integer representing the vertex
			@param vi Path via other vertex number
			@param d Distance between this vertex and the other
			@param c Cost of the flight between the vertices 
		*/
		public Edge (int v, int vi, int d, double c)
		{	vertex = v; via = vi; dist = d; hops = 0; cost = c;}

		/** 
			Public constructor
			@param v Integer representing the vertex
			@param vi Path via other vertex number
			@param d Distance between this vertex and the other
			@param h Number of hops from the starting city along a path
			@param c Cost of the flight between the vertices 
		*/
		public Edge (int v, int vi, int d, int h, double c)
		{	vertex = v; via = vi; dist = d; hops = h; cost = c;}

		/**
			Compares two Edges and returns the difference in their distances.
			@return Difference between two edges' distances.
		*/
		public int compareTo (Edge other)
		{	return dist - other.dist; }

		/** 
			Returns a formatted string representing the Edge's contents.
			@return Formatted string representing the Edge's contents 
		*/
		public String toString()
		{
			String ret = null;
			sb.append("(vertex = "); 	sb.append(vertex);
			sb.append(", via = "); 		sb.append(via);
			sb.append(", dist = ");		sb.append(dist);
			sb.append(", cost = ");		sb.append(cost);
			sb.append(")");

			ret = sb.toString(); sb.setLength(0);
			return ret;
		}
	}

	/** Class used to sort the edges of an undirected edge-weighted graph 
	by their distance.*/
	static class ByDist implements Comparator<Edge>
	{
		/**
			Compares two graph vertices (Edge objects) and returns the
			difference between their distances.
			@return Difference in distance
		*/		
	    public int compare(Edge a, Edge b) 
	    { 	return a.dist - b.dist; } 
	}

	/** Class used to sort the edges of an undirected edge-weighted graph 
	by their cost. */
	static class ByCost implements Comparator<Edge>
	{
		/**
			Compares two graph vertices (Edge objects) and returns the
			difference between their cost.
			@return Difference in cost
		*/
	    public int compare(Edge a, Edge b) 
	    { 	return (int)(a.cost - b.cost);} 
	}

	/** Class used to sort the edges of an undirected edge-weighted graph 
		by hops.*/
	static class ByHops implements Comparator<Edge>
	{
		/**
			Compares two graph vertices (Edge objects) and returns the
			difference between their hops.
			@return Difference in hops
		*/
	    public int compare(Edge a, Edge b) 
	    { 	// Prefer cheap flights if hops is same
	    	int val = a.hops - b.hops; 
	        return val != 0? val: (int)(a.cost - b.cost); 
	    } 
	}	
}