package gameObjects;

import java.io.Serializable;

import javafx.scene.layout.StackPane;
/**
* <h1>Tile</h1>
* abstract class that other tiles extend
* 
* @author  Ali Habbabeh
* @version 1.2
* @since   2016-12-22
*/
public abstract class Tile extends StackPane implements Serializable {
	// protected to prevent mutation (create multiple instances of the same object )
	//which i read that it's good for garbage collection
	private  int tileId;
	private int col;
	private int row;
	private Pawn pawn;
	
	//constructor
	public Tile(int tileId){
		super();
		this.tileId=tileId;
		
	}
	// another constructor with locatin 
	public Tile(int tileId, int col, int row){
		super();
		this.tileId=tileId;
		this.col=col;
		this.row=row;
		
	}
	
	public int getTileId() {
		return tileId;
	}

	public void setTileId(int id){
		tileId=id;
	}

	public boolean hasPawn(){
		return pawn !=null;
	}
	
	public Pawn getPawn(){
		return pawn;
	}
	
	public void setPawn(Pawn pawn){
		this.pawn=pawn;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	

	
}
