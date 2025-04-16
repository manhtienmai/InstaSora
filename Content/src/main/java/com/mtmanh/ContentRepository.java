package com.mtmanh;

import javax.swing.text.AbstractDocument;

public interface ContentRepository extends JpaRepository<AbstractDocument.Content, Integer> {
}
