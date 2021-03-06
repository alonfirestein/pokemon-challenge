package gameClient;

import api.*;
import gameClient.util.*;
import org.json.JSONObject;

import java.util.Objects;

/**
 * This class was created to implement the Agent whose job is to locate capture the Pokemons that are located
 * on the graph.
 */
public class Agent {
		public static final double EPS = 0.0001;
		private static int _count = 0;
		private static int _seed = 3331;
		private int _id;
	//	private long _key;
		private geo_location _pos;
		private double _speed;
		private edge_data _curr_edge;
		private node_data _curr_node;
		private directed_weighted_graph graph;
		private Pokemon _curr_fruit;
		private long _sg_dt;
		private double _value;
		
		
		public Agent(directed_weighted_graph g, int start_node) {
			graph = g;
			setMoney(0);
			this._curr_node = graph.getNode(start_node);
			_pos = _curr_node.getLocation();
			_id = -1;
			setSpeed(0);
		}

		public void update(String json) {
			JSONObject line;
			try {
				// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
				line = new JSONObject(json);
				JSONObject agent = line.getJSONObject("Agent");
				int id = agent.getInt("id");
				if(id == this.getID() || this.getID() == -1) {
					if(this.getID() == -1) {_id = id;}
					double speed = agent.getDouble("speed");
					String p = agent.getString("pos");
					Point3D pp = new Point3D(p);
					int src = agent.getInt("src");
					int dest = agent.getInt("dest");
					double value = agent.getDouble("value");
					this._pos = pp;
					this.setCurrNode(src);
					this.setSpeed(speed);
					this.setNextNode(dest);
					this.setMoney(value);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		//@Override
		public int getSrcNode() {return this._curr_node.getKey();}
		public String toJSON() {
			int d = this.getNextNode();
			String ans = "{\"Agent\":{"
					+ "\"id\":"+this._id+","
					+ "\"value\":"+this._value+","
					+ "\"src\":"+this._curr_node.getKey()+","
					+ "\"dest\":"+d+","
					+ "\"speed\":"+this.getSpeed()+","
					+ "\"pos\":\""+_pos.toString()+"\""
					+ "}"
					+ "}";
			return ans;	
		}

		private void setMoney(double v) {_value = v;}
	
		public boolean setNextNode(int dest) {
			boolean ans = false;
			int src = this._curr_node.getKey();
			this._curr_edge = graph.getEdge(src, dest);
			if(_curr_edge!=null) {
				ans = true;
			}
			else {
				_curr_edge = null;
			}
			return ans;
		}

		public void setCurrNode(int src) {
			this._curr_node = graph.getNode(src);
		}

		public boolean isMoving() {
			return this._curr_edge != null;
		}

		public String toString() {
			return toJSON();
		}

		public String toString1() {
			String ans=""+this.getID()+","+_pos+", "+isMoving()+","+this.getValue();	
			return ans;
		}

		public int getID() {
			return this._id;
		}
	
		public geo_location getLocation() {
			return _pos;
		}

		
		public double getValue() {
			return this._value;
		}



		public int getNextNode() {
			if (this._curr_edge==null) {
				return -1;
			}
			return this._curr_edge.getDest();
		}

		public double getSpeed() {
			return this._speed;
		}

		public void setSpeed(double v) {
			this._speed = v;
		}

		public Pokemon get_curr_fruit() {
			return _curr_fruit;
		}

		public void set_curr_fruit(Pokemon curr_fruit) {
			this._curr_fruit = curr_fruit;
		}

		public void set_SDT(long ddtt) {
			long ddt = ddtt;
			if(this._curr_edge!=null) {
				double w = get_curr_edge().getWeight();
				geo_location dest = graph.getNode(get_curr_edge().getDest()).getLocation();
				geo_location src = graph.getNode(get_curr_edge().getSrc()).getLocation();
				double de = src.distance(dest);
				double dist = _pos.distance(dest);
				if(this.get_curr_fruit().get_edge()==this.get_curr_edge()) {
					 dist = _curr_fruit.getLocation().distance(this._pos);
				}
				double norm = dist/de;
				double dt = w*norm / this.getSpeed(); 
				ddt = (long)(1000.0*dt);
			}
			this.set_sg_dt(ddt);
		}
		
		public edge_data get_curr_edge() {
			return this._curr_edge;
		}

		public long get_sg_dt() {
			return _sg_dt;
		}

		public void set_sg_dt(long _sg_dt) {
			this._sg_dt = _sg_dt;
		}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Agent _agent = (Agent) o;
		return _id == _agent._id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id);
	}
}
