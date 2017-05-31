package de.clemenskeppler.materialsearchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Dimension;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Clemens Keppler on 26.05.2017. Base class for the search toolbar. Use this class in your XML layout and
 * set the desired attributes. If you wish to change the contained TextView or progressbar, simply extend this class
 * and create your own view.
 */
public class MaterialSearchView extends RelativeLayout {
  private static final int DEFAULT_CIRCULAR_REVEAL_ANIMATION_DURATION = 250;
  private static final int HEIGHT_NOT_DEFINED = -1;
  protected ProgressBar progress;
  protected TextView noResultsFound;
  private MenuItem menuItemSearch;
  private int animationDuration;
  private Toolbar toolbar;
  private CardView searchResultsContainer;
  private int positionFromRight;
  private boolean hasOverflow;
  private int positionOffset;
  private int overflowOffset;
  private Animation slideDown;
  private boolean cancelOnTouchOutside;
  private SearchView searchView;
  private RecyclerView searchResults;
  private Path revealPath;
  private float clipRadius;
  private boolean clipOutlines;
  @Dimension private int searchbarHeight;
  @Dimension private int defaultActionBarHeight;
  private String noResultsFoundHint;
  private Point displaySize;
  private Animator.AnimatorListener circleAnimShowListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
      super.onAnimationStart(animation);
      searchResultsContainer.setVisibility(VISIBLE);
      searchResultsContainer.startAnimation(slideDown);
    }
  };
  private Animator.AnimatorListener circleAnimHideListener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationStart(Animator animation) {
      super.onAnimationStart(animation);
      searchResultsContainer.setVisibility(GONE);
    }

    @Override public void onAnimationEnd(Animator animation) {
      super.onAnimationEnd(animation);
      setVisibility(GONE);
      clipOutlines = false;
    }
  };
  private int cy;
  private int cx;
  private int width;

  public MaterialSearchView(Context context) {
    super(context);
    positionFromRight = 1;
    inflate(context, R.layout.search_toolbar, this);
  }

  public MaterialSearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
    inflate(context, R.layout.search_toolbar, this);
  }

  public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
    inflate(context, R.layout.search_toolbar, this);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    setUpViews();
    setUpSearchToolbar();
    width = toolbar.getWidth() - positionOffset - overflowOffset;
    if (width <= 0) {
      width += displaySize.x - getPaddingLeft() - getPaddingRight();
    }
  }

  public void hide() {
    circularHide();
    menuItemSearch.collapseActionView();
  }

  public void show() {
    circularReveal();
    menuItemSearch.expandActionView();
  }

  public void showProgress() {
    progress.setVisibility(VISIBLE);
  }

  public void hideProgress() {
    progress.setVisibility(GONE);
  }

  public void showNoResultsFoundHint() {
    noResultsFound.setVisibility(VISIBLE);
  }

  public void hideNoResultsFoundHint() {
    noResultsFound.setVisibility(GONE);
  }

  public SearchView getSearchView() {
    return searchView;
  }

  public RecyclerView getSearchResults() {
    return searchResults;
  }


  private void setClipRadius(final float clipRadius) {
    this.clipRadius = clipRadius;
    invalidate();
  }

  private void circularReveal() {
    Animator anim;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, (float) width);
    } else {
      clipOutlines = true;
      anim = ObjectAnimator.ofFloat(this, "ClipRadius", 0, (float) width);
    }
    anim.addListener(circleAnimShowListener);
    anim.setDuration(animationDuration);
    setVisibility(VISIBLE);
    anim.start();
  }

  private void circularHide() {
    Animator animator;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, (float) width, 0);
    } else {
      clipOutlines = true;
      animator = ObjectAnimator.ofFloat(this, "ClipRadius", width, 0);
    }
    animator.addListener(circleAnimHideListener);
    animator.setDuration(animationDuration);
    animator.start();
  }


  @Override public void draw(Canvas canvas) {
    if (!clipOutlines) {
      super.draw(canvas);
      return;
    }
    final int state = canvas.save();
    revealPath.reset();
    revealPath.addCircle(cx, cy, clipRadius, Path.Direction.CW);
    canvas.clipPath(revealPath);
    super.draw(canvas);
    canvas.restoreToCount(state);
  }


  private void init(Context context, AttributeSet attrs) {
    setWillNotDraw(false);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, 0, 0);

    try {
      positionFromRight = a.getInt(R.styleable.MaterialSearchView_searchIconPositionFromRight, 0);
      hasOverflow = a.getBoolean(R.styleable.MaterialSearchView_hasOverflow, false);
      searchbarHeight = a.getDimensionPixelSize(R.styleable.MaterialSearchView_searchBarHeight, HEIGHT_NOT_DEFINED);
      cancelOnTouchOutside = a.getBoolean(R.styleable.MaterialSearchView_cancelOnTouchOutside, true);
      noResultsFoundHint = a.getString(R.styleable.MaterialSearchView_noResultsFoundHint);
      animationDuration = a.getInteger(R.styleable.MaterialSearchView_animationTime,
          DEFAULT_CIRCULAR_REVEAL_ANIMATION_DURATION);
    } finally {
      a.recycle();
    }
    Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    displaySize = new Point();
    display.getSize(displaySize);
    setUpVariables();
  }

  private void setUpVariables() {
    Resources resources = getResources();
    if (positionFromRight > 0) {
      positionOffset = (positionFromRight
          * resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))
          + resources.getDimensionPixelSize(R.dimen.abc_button_padding_horizontal_material) / 2;
    }
    positionOffset += getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2;
    if (hasOverflow) {
      overflowOffset = resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
    }
    TypedValue tv = new TypedValue();
    if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      defaultActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.getDisplayMetrics());
    }
    cx = displaySize.x - positionOffset - overflowOffset;
    cy = (defaultActionBarHeight / 2);
    slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
    revealPath = new Path();
    clipOutlines = false;
  }

  private void setUpViews() {
    toolbar = (Toolbar) findViewById(R.id.search_toolbar);
    searchResults = (RecyclerView) findViewById(R.id.search_results);
    if (searchbarHeight != HEIGHT_NOT_DEFINED) {
      RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
      layoutParams.height = searchbarHeight;
      toolbar.setLayoutParams(layoutParams);
    }
    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);
    if (cancelOnTouchOutside) {
      relativeLayout.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if (!v.equals(MaterialSearchView.this)) {
            hide();
          }
        }
      });
    }
    searchResultsContainer = (CardView) findViewById(R.id.search_results_container);
    progress = (ProgressBar) findViewById(R.id.progressBar);
    noResultsFound = (TextView) findViewById(R.id.no_results_found);
    if (noResultsFoundHint != null) {
      noResultsFound.setText(noResultsFoundHint);
    }
  }

  private void setUpSearchToolbar() {
    toolbar.inflateMenu(R.menu.menu_search);
    Menu searchMenu = toolbar.getMenu();

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        circularHide();
      }
    });

    menuItemSearch = searchMenu.findItem(R.id.action_filter_search);
    searchView = (SearchView) menuItemSearch.getActionView();
    MenuItemCompat.setOnActionExpandListener(menuItemSearch, new MenuItemCompat.OnActionExpandListener() {
      @Override public boolean onMenuItemActionCollapse(MenuItem item) {
        circularHide();
        return true;
      }

      @Override public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }
    });
  }
}
