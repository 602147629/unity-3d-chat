using UnityEngine;
using System.Collections;

public class Delete : MonoBehaviour {

	public UIGrid grid;
	
	void Start()
	{
		//得到grid对象
		grid = GameObject.Find("Panel").GetComponent<UIGrid>();
		
	
		
	
	}
	
	
	void OnClick()
	{
		//通过标签名称找到多有对象，前提是给预设起一个tag，这里我叫它player
		GameObject []items =  GameObject.FindGameObjectsWithTag("Player");
		//当预设数量大于 0时
		if(items.Length >0)
		{
			//删除列表的item
			Destroy(items[0]);
			//刷新UI
			grid.repositionNow = true;
		}
		
	}
	
}
