# MaterialSearchView
This is a library which wraps a `SearchView` to reveal itself in a circular fashion, just like in the PlayStore.

![](/art/example_1.gif)
![](/art/example_2.gif)

At its core you can get a `RecyclerView` for the search results and attach your favourite adapter and get the 
`SearchView` to style it like you prefer.

# Usage

Simply include the class `MaterialSearchView` in your XML layout where you define your toolbar.

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical"
  tools:context="de.clemenskeppler.materialsearchview.sample.MainActivity">

  <include layout="@layout/toolbar"/>

  <de.clemenskeppler.materialsearchview.MaterialSearchView
    android:id="@+id/materialSearchView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_centerHorizontal="true"
    android:visibility="gone"/>

  <TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    android:text="Hello World!"/>

</RelativeLayout>
```

For showing the `SearchView` simple call `MaterialSearchView.show()` whenever you desire. E.g.

```Java
public class MainActivity extends AppCompatActivity {

  MaterialSearchView materialSearchView;
  SearchView searchView;
  RecyclerView results;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    materialSearchView = (MaterialSearchView) findViewById(R.id.materialSearchView);
    searchView = materialSearchView.getSearchView();
    results = materialSearchView.getSearchResults();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_filter_search) {
      materialSearchView.show();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
```

Furthermore, you can define
 several XML attributes to change its behaviour.
 
 | Attribute                   | Default         | Description                                                                                            |
 |-----------------------------|-----------------|--------------------------------------------------------------------------------------------------------|
 | hasOverflow                 | false           | Specify if the overflow menu is visible the menu where MaterialSearchView is included                  |
 | searchIconPositionFromRight | 0               | The position of the search icon (i.e. magnifying glass) starting from 0 not counting the overflow menu |
 | searchBarHeight             | Same as toolbar | The height of the searchbar/searchview                                                                 |
 | cancelOnOutsideTouch        | true            | Close the search menu and results on an outside click (similar to `Dialog`)                            |
 | circularAnimationTime       | 300             | The animation time in ms for the circular reveal and hide                                              |
 | hideOnKeyboardClose         | true            | Close the search dialog when the softkey keyboard is minimsed                                          |
 
 
 # Limitations
 On API 16 - 17 the circular reveal and hide animations do not look like a circle but like a square. Hopefully this 
 can be fixed in later releases somehow.