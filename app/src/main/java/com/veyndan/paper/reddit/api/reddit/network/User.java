package com.veyndan.paper.reddit.api.reddit.network;

public enum User {
    COMMENTS {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return comments == submitted && gilded;
        }
    },
    DOWNVOTED {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    GILDED {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    HIDDEN {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    OVERVIEW {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    SAVED {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    SUBMITTED {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    },
    UPVOTED {
        @Override
        public boolean isForConstant(final boolean comments, final boolean submitted, final boolean gilded) {
            return false;
        }
    };

    public abstract boolean isForConstant(boolean comments, boolean submitted, boolean gilded);

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }

    public static User fromOptions(final boolean comments,
                                   final boolean submitted,
                                   final boolean gilded) {
        if (comments && submitted && gilded || !comments && !submitted && gilded) {
            return GILDED;
        } else if (comments && !submitted && gilded || !comments && submitted && gilded) {
            throw new UnsupportedOperationException("User state unsure");
        } else if (comments && submitted && !gilded) {
            return OVERVIEW;
        } else if (comments && !submitted && !gilded) {
            return COMMENTS;
        } else if (submitted && !comments && !gilded) {
            return SUBMITTED;
        } else {
            throw new UnsupportedOperationException("User state unsure");
        }
    }
}
