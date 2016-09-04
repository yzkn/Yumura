
import android.support.v7.util.SortedList;

import java.util.List;

import twitter4j.Status;
import twitter4j.User;

public final class BinarySearchUtil {
	public static final int binary_search(final long needle, final SortedList<Status> tweets) {
		int low = 0;
		int high = tweets.size() - 1;
		int mid;

		while (low <= high) {
			mid = ( low + high ) / 2;
			if (needle == tweets.get(mid).getId()) {
				return mid;
			} else if (needle > tweets.get(mid).getId()) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return -1;
	}
}