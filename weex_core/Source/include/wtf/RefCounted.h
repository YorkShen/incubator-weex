/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
#pragma once

#include <wtf/Assertions.h>
#include <wtf/FastMalloc.h>
#include <wtf/Noncopyable.h>

namespace WTF {

#if defined(NDEBUG) && !ENABLE(SECURITY_ASSERTIONS)
#define CHECK_REF_COUNTED_LIFECYCLE 0
#else
#define CHECK_REF_COUNTED_LIFECYCLE 1
#endif

// This base class holds the non-template methods and attributes.
// The RefCounted class inherits from it reducing the template bloat
// generated by the compiler (technique called template hoisting).
class RefCountedBase {
public:
    void ref() const
    {
#if CHECK_REF_COUNTED_LIFECYCLE
        ASSERT_WITH_SECURITY_IMPLICATION(!m_deletionHasBegun);
        ASSERT(!m_adoptionIsRequired);
#endif
        ++m_refCount;
    }

    bool hasOneRef() const
    {
#if CHECK_REF_COUNTED_LIFECYCLE
        ASSERT(!m_deletionHasBegun);
#endif
        return m_refCount == 1;
    }

    unsigned refCount() const
    {
        return m_refCount;
    }

    void relaxAdoptionRequirement()
    {
#if CHECK_REF_COUNTED_LIFECYCLE
        ASSERT_WITH_SECURITY_IMPLICATION(!m_deletionHasBegun);
        ASSERT(m_adoptionIsRequired);
        m_adoptionIsRequired = false;
#endif
    }

protected:
    RefCountedBase()
        : m_refCount(1)
#if CHECK_REF_COUNTED_LIFECYCLE
        , m_deletionHasBegun(false)
        , m_adoptionIsRequired(true)
#endif
    {
    }

    ~RefCountedBase()
    {
#if CHECK_REF_COUNTED_LIFECYCLE
        ASSERT(m_deletionHasBegun);
        ASSERT(!m_adoptionIsRequired);
#endif
    }

    // Returns whether the pointer should be freed or not.
    bool derefBase() const
    {
#if CHECK_REF_COUNTED_LIFECYCLE
        ASSERT_WITH_SECURITY_IMPLICATION(!m_deletionHasBegun);
        ASSERT(!m_adoptionIsRequired);
#endif

        ASSERT(m_refCount);
        unsigned tempRefCount = m_refCount - 1;
        if (!tempRefCount) {
#if CHECK_REF_COUNTED_LIFECYCLE
            m_deletionHasBegun = true;
#endif
            return true;
        }
        m_refCount = tempRefCount;
        return false;
    }

#if CHECK_REF_COUNTED_LIFECYCLE
    bool deletionHasBegun() const
    {
        return m_deletionHasBegun;
    }
#endif

private:

#if CHECK_REF_COUNTED_LIFECYCLE
    friend void adopted(RefCountedBase*);
#endif

    mutable unsigned m_refCount;
#if CHECK_REF_COUNTED_LIFECYCLE
    mutable bool m_deletionHasBegun;
    mutable bool m_adoptionIsRequired;
#endif
};

#if CHECK_REF_COUNTED_LIFECYCLE
inline void adopted(RefCountedBase* object)
{
    if (!object)
        return;
    ASSERT_WITH_SECURITY_IMPLICATION(!object->m_deletionHasBegun);
    object->m_adoptionIsRequired = false;
}
#endif

template<typename T> class RefCounted : public RefCountedBase {
    WTF_MAKE_NONCOPYABLE(RefCounted); WTF_MAKE_FAST_ALLOCATED;
public:
    void deref() const
    {
        if (derefBase())
            delete static_cast<const T*>(this);
    }

protected:
    RefCounted() { }
    ~RefCounted()
    {
    }
};

} // namespace WTF

using WTF::RefCounted;